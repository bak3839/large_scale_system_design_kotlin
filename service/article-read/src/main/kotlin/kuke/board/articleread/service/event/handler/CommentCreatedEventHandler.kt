package kuke.board.articleread.service.event.handler

import kuke.board.articleread.repository.ArticleQueryModel
import kuke.board.articleread.repository.ArticleQueryModelRepository
import kuke.board.common.event.Event
import kuke.board.common.event.EventType
import kuke.board.common.event.payload.ArticleCreatedEventPayload
import kuke.board.common.event.payload.ArticleDeletedEventPayload
import kuke.board.common.event.payload.CommentCreatedEventPayload
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class CommentCreatedEventHandler(
    private val articleQueryModelRepository: ArticleQueryModelRepository
): EventHandler<CommentCreatedEventPayload> {
    override fun handle(event: Event<CommentCreatedEventPayload>) {
        val payload = event.payload
        articleQueryModelRepository.read(event.payload.articleId)?.let {
            it.updateBy(payload)
            articleQueryModelRepository.update(it)
        }
    }

    override fun supports(event: Event<CommentCreatedEventPayload>): Boolean
    = EventType.COMMENT_CREATED == event.type
}