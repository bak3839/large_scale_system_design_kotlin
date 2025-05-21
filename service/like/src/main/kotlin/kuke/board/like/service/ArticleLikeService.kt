package kuke.board.like.service

import kuke.board.common.event.EventType
import kuke.board.common.event.payload.ArticleLikedEventPayload
import kuke.board.common.event.payload.ArticleUnlikedEventPayload
import kuke.board.common.outboxmessagerelay.OutboxEventPublisher
import kuke.board.common.snowflake.Snowflake
import kuke.board.like.entity.ArticleLike
import kuke.board.like.entity.ArticleLikeCount
import kuke.board.like.repository.ArticleLikeCountRepository
import kuke.board.like.repository.ArticleLikeRepository
import kuke.board.like.service.response.ArticleLikeResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ArticleLikeService(
    private val articleLikeRepository: ArticleLikeRepository,
    private val articleLikeCountRepository: ArticleLikeCountRepository,
    private val outboxEventPublisher: OutboxEventPublisher
) {
    private val snowflake = Snowflake()

    fun read(articleId: Long, userId: Long) : ArticleLikeResponse {
        return articleLikeRepository.findByArticleIdAndUserId(articleId, userId)
            ?.let(ArticleLikeResponse::from)
            ?: throw NoSuchElementException()
    }

    /**
     * update 구문
     */
    @Transactional
    fun likePessimisticLock1(articleId: Long, userId: Long) {
        val articleLike = articleLikeRepository.save(
            ArticleLike.create(
                articleLikeId = snowflake.nextId(),
                articleId = articleId,
                userId = userId
            )
        )

        val result = articleLikeCountRepository.increase(articleId)

        if(result == 0) {
            // 최초 요청 시에는 update 되는 레코드가 없으므로, 1로 초기화
            // 트래픽이 순식간에 몰릴 수 있는 상황에는 유실될 수 있으므로, 게시글 생성 시점에 미리 0으로 초기화 해둘 수 있음
            articleLikeCountRepository.save(
                ArticleLikeCount.init(articleId, 1L)
            )
        }

        outboxEventPublisher.publish(
            EventType.ARTICLE_LIKED,
            ArticleLikedEventPayload(
                articleLikeId = articleLike.articleLikeId,
                articleId = articleLike.articleId,
                userId = articleLike.userId,
                createdAt = articleLike.createdAt,
                articleLikeCount = count(articleLike.articleId)
            ),
            articleLike.articleId
        )
    }

    @Transactional
    fun unlikePessimisticLock1(articleId: Long, userId: Long) {
        articleLikeRepository.findByArticleIdAndUserId(articleId, userId)
            ?.let {
                articleLikeRepository.delete(it)
                articleLikeCountRepository.decrease(articleId)

                outboxEventPublisher.publish(
                    EventType.ARTICLE_UNLIKED,
                    ArticleUnlikedEventPayload(
                        articleLikeId = it.articleLikeId,
                        articleId = it.articleId,
                        userId = it.userId,
                        createdAt = it.createdAt,
                        articleLikeCount = count(it.articleId)
                    ),
                    it.articleId
                )
            }
    }

    /**
     * select ... for update -> update 구문
     */
    @Transactional
    fun likePessimisticLock2(articleId: Long, userId: Long) {
        val articleLike = articleLikeRepository.save(
            ArticleLike.create(
                articleLikeId = snowflake.nextId(),
                articleId = articleId,
                userId = userId
            )
        )

        val articleLikeCount = (articleLikeCountRepository.findLockedByArticleId(articleId)
            ?: ArticleLikeCount.init(articleId, 0L))
        articleLikeCount.increase()
        articleLikeCountRepository.save(articleLikeCount)
    }

    @Transactional
    fun unlikePessimisticLock2(articleId: Long, userId: Long) {
        articleLikeRepository.findByArticleIdAndUserId(articleId, userId)
            ?.let {
                articleLikeRepository.delete(it)
                val articleLikeCount = articleLikeCountRepository.findLockedByArticleId(articleId)
                    ?: throw NoSuchElementException()
                articleLikeCount.decrease()
            }
    }

    @Transactional
    fun likeOptimisticLock(articleId: Long, userId: Long) {
        val articleLike = articleLikeRepository.save(
            ArticleLike.create(
                articleLikeId = snowflake.nextId(),
                articleId = articleId,
                userId = userId
            )
        )

        val articleLikeCount = (articleLikeCountRepository.findByArticleId(articleId)
            ?: ArticleLikeCount.init(articleId, 0L))
        articleLikeCount.increase()
        articleLikeCountRepository.save(articleLikeCount)
    }

    @Transactional
    fun unlikeOptimisticLock(articleId: Long, userId: Long) {
        articleLikeRepository.findByArticleIdAndUserId(articleId, userId)
            ?.let {
                articleLikeRepository.delete(it)
                val articleLikeCount = articleLikeCountRepository.findByArticleId(articleId)
                    ?: throw NoSuchElementException()
                articleLikeCount.decrease()
            }
    }

    fun count(articleId: Long): Long
    = articleLikeCountRepository.findById(articleId)
        .map(ArticleLikeCount::likeCount)
        .orElse(0L)
}