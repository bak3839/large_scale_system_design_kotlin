package kuke.board.hotarticle.service.eventhandler

import kuke.board.common.event.Event
import kuke.board.common.event.EventType
import kuke.board.common.event.payload.CommentCreatedEventPayload
import kuke.board.hotarticle.repository.ArticleCommentCountRepository
import kuke.board.hotarticle.utils.TimeCalculatorUtils

class CommentCreatedEventHandler(
    private val articleCommentCountRepository: ArticleCommentCountRepository
): EventHandler<CommentCreatedEventPayload> {
    override fun handle(event: Event<CommentCreatedEventPayload>) {
        val payload = event.payload
        articleCommentCountRepository.createOrUpdate(
            articleId = payload.articleId,
            commentCount = payload.articleCommentCount,
            ttl = TimeCalculatorUtils.calculateDurationToMidnight()
        )
    }

    override fun supports(event: Event<CommentCreatedEventPayload>): Boolean
    = EventType.COMMENT_CREATED == event.type

    override fun findArticleId(event: Event<CommentCreatedEventPayload>): Long
    = event.payload.articleId
}