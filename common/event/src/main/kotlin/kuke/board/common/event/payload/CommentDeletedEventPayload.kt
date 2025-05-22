package kuke.board.common.event.payload

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import kuke.board.common.event.EventPayload
import java.time.LocalDateTime

class CommentDeletedEventPayload @JsonCreator constructor(
    @JsonProperty("commentId") val commentId: Long,
    @JsonProperty("content") val content: String,
    @JsonProperty("path") val path: String,
    @JsonProperty("articleId") val articleId: Long,
    @JsonProperty("writerId") val writerId: Long,
    @JsonProperty("deleted") val deleted: Boolean,
    @JsonProperty("createdAt") val createdAt: LocalDateTime,
    @JsonProperty("articleCommentCount") val articleCommentCount: Long
): EventPayload