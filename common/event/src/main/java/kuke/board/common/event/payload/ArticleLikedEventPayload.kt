package kuke.board.common.event.payload

import kuke.board.common.event.EventPayload
import java.time.LocalDateTime

class ArticleLikedEventPayload(
    private val articleLikeId: Long,
    private val articleId: Long,
    private val userId: Long,
    private val createdAt: LocalDateTime,
    private val articleLikeCount: Long
): EventPayload {
}