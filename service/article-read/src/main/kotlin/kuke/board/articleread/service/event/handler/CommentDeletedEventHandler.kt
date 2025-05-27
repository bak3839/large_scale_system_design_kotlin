package kuke.board.articleread.service.event.handler

import kuke.board.articleread.repository.ArticleQueryModel
import kuke.board.articleread.repository.ArticleQueryModelRepository
import kuke.board.common.event.Event
import kuke.board.common.event.EventType
import kuke.board.common.event.payload.ArticleCreatedEventPayload
import kuke.board.common.event.payload.ArticleDeletedEventPayload
import kuke.board.common.event.payload.CommentCreatedEventPayload
import kuke.board.common.event.payload.CommentDeletedEventPayload
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class CommentDeletedEventHandler(
    private val articleQueryModelRepository: ArticleQueryModelRepository
): EventHandler<CommentDeletedEventPayload> {
    override fun handle(event: Event<CommentDeletedEventPayload>) {
        val payload = event.payload
        articleQueryModelRepository.read(event.payload.articleId)?.let {
            it.updateBy(payload)
            articleQueryModelRepository.update(it)
        }
    }

    override fun supports(event: Event<CommentDeletedEventPayload>): Boolean
    = EventType.COMMENT_DELETED == event.type
}