package kuke.board.article.service

import kuke.board.article.entity.Article
import kuke.board.article.entity.BoardArticleCount
import kuke.board.article.repository.ArticleRepository
import kuke.board.article.repository.BoardArticleCountRepository
import kuke.board.article.service.request.ArticleCreateRequest
import kuke.board.article.service.request.ArticleUpdateRequest
import kuke.board.article.service.response.ArticlePageResponse
import kuke.board.article.service.response.ArticleResponse
import kuke.board.common.event.EventType
import kuke.board.common.event.payload.ArticleCreatedEventPayload
import kuke.board.common.event.payload.ArticleDeletedEventPayload
import kuke.board.common.event.payload.ArticleUpdatedEventPayload
import kuke.board.common.outboxmessagerelay.OutboxEventPublisher
import kuke.board.common.snowflake.Snowflake
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ArticleService(
    private val articleRepository: ArticleRepository,
    private val boardArticleCountRepository: BoardArticleCountRepository,
    private val outboxEventPublisher: OutboxEventPublisher
) {
    private val snowflake: Snowflake = Snowflake()

    @Transactional
    fun create(request: ArticleCreateRequest): ArticleResponse {
        val article = articleRepository.save(
            Article(
                snowflake.nextId(),
                request.title,
                request.content,
                request.boardId,
                request.writerId
            )
        )

        val result = boardArticleCountRepository.increase(request.boardId)
        if(result == 0) {
            boardArticleCountRepository.save(
                BoardArticleCount.init(boardId = request.boardId, articleCount = 1L)
            )
        }

        outboxEventPublisher.publish(
            EventType.ARTICLE_CREATED,
            ArticleCreatedEventPayload(
                articleId = article.articleId,
                title = article.title,
                content = article.content,
                boardId = article.boardId,
                writerId = article.writerId,
                createdAt = article.createdAt,
                modifiedAt = article.modifiedAt,
                boardArticleCount = count(article.boardId)
            ),
            article.boardId
        )
        return ArticleResponse.from(article)
    }

    @Transactional
    fun update(articleId: Long, request: ArticleUpdateRequest): ArticleResponse {
        val article = articleRepository.findById(articleId).orElseThrow()
        article.update(request.title, request.content)
        outboxEventPublisher.publish(
            EventType.ARTICLE_UPDATED,
            ArticleUpdatedEventPayload(
                articleId = article.articleId,
                title = article.title,
                content = article.content,
                boardId = article.boardId,
                writerId = article.writerId,
                createdAt = article.createdAt,
                modifiedAt = article.modifiedAt
            ),
            article.boardId
        )
        return ArticleResponse.from(article)
    }

    fun read(articleId: Long): ArticleResponse
    = ArticleResponse.from(articleRepository.findById(articleId).orElseThrow())

    @Transactional
    fun delete(articleId: Long) {
        val article = articleRepository.findById(articleId).orElseThrow()
        articleRepository.delete(article)
        boardArticleCountRepository.decrease(article.boardId)
        outboxEventPublisher.publish(
            EventType.ARTICLE_DELETED,
            ArticleDeletedEventPayload(
                articleId = article.articleId,
                title = article.title,
                content = article.content,
                boardId = article.boardId,
                writerId = article.writerId,
                createdAt = article.createdAt,
                modifiedAt = article.modifiedAt,
                boardArticleCount = count(article.boardId)
            ),
            article.boardId
        )
    }

    fun readAll(boardId: Long, page: Long, pageSize: Long) :ArticlePageResponse {
        return ArticlePageResponse.of(
            articleRepository.findAll(boardId, (page - 1) * pageSize, pageSize)
                .map(ArticleResponse::from)
                .toList(),
            articleRepository.count(
                boardId,
                PageCalculator.calculatePageLimit(page, pageSize, 10L)
            )
        )
    }

    fun readAllInfiniteScroll(boardId: Long, pageSize: Long, lastArticleId: Long?):List<ArticleResponse> {
        val articles = lastArticleId?.let {
            articleRepository.findAllInfiniteScroll(boardId, pageSize, lastArticleId)
        } ?: articleRepository.findAllInfiniteScroll(boardId, pageSize)

        return articles.map(ArticleResponse::from).toList()
    }

    fun count(boardId: Long): Long
    = boardArticleCountRepository.findById(boardId)
        .map(BoardArticleCount::articleCount)
        .orElse(0L)
}