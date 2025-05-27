package kuke.board.articleread.service.event.handler

import kuke.board.articleread.repository.ArticleQueryModel
import kuke.board.articleread.repository.ArticleQueryModelRepository
import kuke.board.common.event.Event
import kuke.board.common.event.EventType
import kuke.board.common.event.payload.ArticleCreatedEventPayload
import kuke.board.common.event.payload.ArticleDeletedEventPayload
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class ArticleDeletedEventHandler(
    private val articleQueryModelRepository: ArticleQueryModelRepository
): EventHandler<ArticleDeletedEventPayload> {
    override fun handle(event: Event<ArticleDeletedEventPayload>) {
        val payload = event.payload
        articleQueryModelRepository.delete(payload.articleId)
    }

    override fun supports(event: Event<ArticleDeletedEventPayload>): Boolean
    = EventType.ARTICLE_DELETED == event.type
}