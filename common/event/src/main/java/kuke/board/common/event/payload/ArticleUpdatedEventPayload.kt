package kuke.board.common.event.payload

import kuke.board.common.event.EventPayload
import java.time.LocalDateTime

class ArticleUpdatedEventPayload(
    private val articleId: Long,
    private val title: String,
    private val content: String,
    private val boardId: Long,
    private val writerId: Long,
    private val createdAt: LocalDateTime,
    private val modifiedAt: LocalDateTime
): EventPayload {
}