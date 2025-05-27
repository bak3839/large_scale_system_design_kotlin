package kuke.board.articleread.service.event.handler

import kuke.board.articleread.repository.ArticleQueryModel
import kuke.board.articleread.repository.ArticleQueryModelRepository
import kuke.board.common.event.Event
import kuke.board.common.event.EventType
import kuke.board.common.event.payload.ArticleCreatedEventPayload
import kuke.board.common.event.payload.ArticleUpdatedEventPayload
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class ArticleUpdatedEventHandler(
    private val articleQueryModelRepository: ArticleQueryModelRepository
): EventHandler<ArticleUpdatedEventPayload> {

    override fun handle(event: Event<ArticleUpdatedEventPayload>) {
        val payload = event.payload
        articleQueryModelRepository.read(event.payload.articleId)?.let {
            it.updateBy(payload)
            articleQueryModelRepository.update(it)
        }
    }

    override fun supports(event: Event<ArticleUpdatedEventPayload>): Boolean
    = EventType.ARTICLE_UPDATED == event.type
}