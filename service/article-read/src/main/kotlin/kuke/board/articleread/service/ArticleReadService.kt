package kuke.board.articleread.service

import kuke.board.articleread.client.ArticleClient
import kuke.board.articleread.client.CommentClient
import kuke.board.articleread.client.LikeClient
import kuke.board.articleread.client.ViewClient
import kuke.board.articleread.repository.ArticleIdListRepository
import kuke.board.articleread.repository.ArticleQueryModel
import kuke.board.articleread.repository.ArticleQueryModelRepository
import kuke.board.articleread.repository.BoardArticleCountRepository
import kuke.board.articleread.service.event.handler.EventHandler
import kuke.board.articleread.service.response.ArticleReadPageResponse
import kuke.board.articleread.service.response.ArticleReadResponse
import kuke.board.common.event.Event
import kuke.board.common.event.EventPayload
import mu.KotlinLogging
import org.springframework.stereotype.Service
import java.time.Duration

@Service
class ArticleReadService(
    private val articleClient: ArticleClient,
    private val commentClient: CommentClient,
    private val viewClient: ViewClient,
    private val likeClient: LikeClient,
    private val articleQueryModelRepository: ArticleQueryModelRepository,
    private val eventHandlers: List<EventHandler<*>>,
    private val articleIdListRepository: ArticleIdListRepository,
    private val boardArticleCountRepository: BoardArticleCountRepository
) {
    private val log = KotlinLogging.logger {}

    fun handleEvent(event: Event<EventPayload>) {
        @Suppress("UNCHECKED_CAST")
        eventHandlers as List<EventHandler<EventPayload>>

        for (eventHandler in eventHandlers) {
            if (eventHandler.supports(event)) {
                eventHandler.handle(event)
            }
        }
    }

    fun read(articleId: Long): ArticleReadResponse {
        val articleQueryModel = articleQueryModelRepository.read(articleId)
            ?: fetch(articleId)
            ?: throw IllegalStateException("Article does not exist")

        return ArticleReadResponse.from(
            articleQueryModel,
            viewClient.count(articleId)
        )
    }

    private fun fetch(articleId: Long): ArticleQueryModel? {
        return articleClient.read(articleId)?.let {
            val articleQueryModel = ArticleQueryModel.create(
                likeClient.count(articleId),
                commentClient.count(articleId),
                it
            )

            log.info { "[ArticleReadService] fetch data. article=$articleId isPresent=true" }
            articleQueryModelRepository.create(articleQueryModel, Duration.ofDays(1))
            articleQueryModel
        }
    }

    fun readAll(boardId: Long, page: Long, pageSize: Long): ArticleReadPageResponse {
        return ArticleReadPageResponse.of(
            readAll(
                readAllArticleIds(boardId, page, pageSize),
            ),
            count(boardId)
        )
    }

    fun readAllArticleIds(boardId: Long, page: Long, pageSize: Long): List<Long> {
        val articleIds = articleIdListRepository.readAll(boardId, (page - 1) * pageSize, pageSize)
        if (pageSize == articleIds.size.toLong()) {
            log.info { "[ArticleReadService.readAllArticleIds] return redis data." }
            return articleIds
        }

        log.info { "[ArticleReadService.readAllArticleIds] return origin data." }
        return articleClient.readAll(boardId, page, pageSize)?.articles
            ?.map(ArticleClient.Companion.ArticleResponse::articleId)
            ?: emptyList()
    }

    fun readAllInfiniteScroll(boardId: Long, lastArticleId: Long, pageSize: Long): List<ArticleReadResponse> {
        return readAll(
            readAllInfiniteScrollArticleIds(boardId, lastArticleId, pageSize),
        )
    }

    fun readAllInfiniteScrollArticleIds(boardId: Long, lastArticleId: Long, pageSize: Long): List<Long> {
        val articleIds = articleIdListRepository.readAll(boardId, lastArticleId, pageSize)
        if(pageSize == articleIds.size.toLong()) {
            log.info { "[ArticleReadService.readAllArticleIds] return redis data." }
            return articleIds
        }

        log.info { "[ArticleReadService.readAllArticleIds] return origin data." }
        return articleClient.readAllInfiniteScroll(boardId, lastArticleId, pageSize)
            ?.map(ArticleClient.Companion.ArticleResponse::articleId)
            ?: emptyList()
    }

    private fun readAll(articleIds: List<Long>): List<ArticleReadResponse> {
        val articleQueryModelMap = articleQueryModelRepository.readAll(articleIds)

        return articleIds.mapNotNull { articleId ->
            if (articleQueryModelMap.contains(articleId)) articleQueryModelMap[articleId]
            else fetch(articleId)
        }.map { articleQueryModel ->
            ArticleReadResponse.from(
                articleQueryModel = articleQueryModel,
                viewCount = viewClient.count(articleQueryModel.articleId)
            )
        }
    }

    private fun count(boardId: Long): Long {
        boardArticleCountRepository.read(boardId)?.let { return it }

        val count = articleClient.count(boardId)
        boardArticleCountRepository.createOrUpdate(boardId, count)
        return count
    }
}