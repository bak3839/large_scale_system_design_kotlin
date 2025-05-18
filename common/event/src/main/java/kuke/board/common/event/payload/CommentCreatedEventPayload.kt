package kuke.board.common.event.payload

import kuke.board.common.event.EventPayload
import java.time.LocalDateTime

class CommentCreatedEventPayload(
    private val commentId: Long,
    private val content: String,
    private val path: String,
    private val articleId: Long,
    private val writerId: Long,
    private val deleted: Boolean,
    private val createdAt: LocalDateTime,
    private val articleCommentCount: Long
): EventPayload {
}