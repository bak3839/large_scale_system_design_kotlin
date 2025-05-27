package kuke.board.articleread.service

import kuke.board.articleread.client.ArticleClient
import kuke.board.articleread.client.CommentClient
import kuke.board.articleread.client.LikeClient
import kuke.board.articleread.client.ViewClient
import kuke.board.articleread.repository.ArticleQueryModel
import kuke.board.articleread.repository.ArticleQueryModelRepository
import kuke.board.articleread.service.event.handler.EventHandler
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
    private val eventHandlers: List<EventHandler<*>>
) {
    private val log = KotlinLogging.logger {}

    fun handleEvent(event: Event<EventPayload>) {
        @Suppress("UNCHECKED_CAST")
        eventHandlers as List<EventHandler<EventPayload>>

        for(eventHandler in eventHandlers) {
            if(eventHandler.supports(event)) {
                eventHandler.handle(event)
            }
        }
    }

    private fun read(articleId: Long): ArticleReadResponse {
        val articleQueryModel = articleQueryModelRepository.read(articleId)?.let { fetch(articleId) }
            ?:throw IllegalStateException("Article query model not found")

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
}