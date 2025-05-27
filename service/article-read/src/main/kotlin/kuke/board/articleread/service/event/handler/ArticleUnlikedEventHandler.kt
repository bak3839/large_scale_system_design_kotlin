package kuke.board.articleread.service.event.handler

import kuke.board.articleread.repository.ArticleQueryModel
import kuke.board.articleread.repository.ArticleQueryModelRepository
import kuke.board.common.event.Event
import kuke.board.common.event.EventType
import kuke.board.common.event.payload.*
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class ArticleUnlikedEventHandler(
    private val articleQueryModelRepository: ArticleQueryModelRepository
): EventHandler<ArticleUnlikedEventPayload> {

    override fun handle(event: Event<ArticleUnlikedEventPayload>) {
        val payload = event.payload
        articleQueryModelRepository.read(event.payload.articleId)?.let {
            it.updateBy(payload)
            articleQueryModelRepository.update(it)
        }
    }

    override fun supports(event: Event<ArticleUnlikedEventPayload>): Boolean
    = EventType.ARTICLE_UNLIKED == event.type
}