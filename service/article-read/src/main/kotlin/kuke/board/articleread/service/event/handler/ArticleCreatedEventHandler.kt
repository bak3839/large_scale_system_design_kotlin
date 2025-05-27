package kuke.board.articleread.service.event.handler

import kuke.board.articleread.repository.ArticleQueryModel
import kuke.board.articleread.repository.ArticleQueryModelRepository
import kuke.board.common.event.Event
import kuke.board.common.event.EventType
import kuke.board.common.event.payload.ArticleCreatedEventPayload
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class ArticleCreatedEventHandler(
    private val articleQueryModelRepository: ArticleQueryModelRepository
): EventHandler<ArticleCreatedEventPayload> {
    override fun handle(event: Event<ArticleCreatedEventPayload>) {
        val payload = event.payload
        articleQueryModelRepository.create(
            ArticleQueryModel.create(payload),
            Duration.ofDays(1)
        )
    }

    override fun supports(event: Event<ArticleCreatedEventPayload>): Boolean
    = EventType.ARTICLE_CREATED == event.type
}