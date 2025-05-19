package kuke.board.common.event.payload

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import kuke.board.common.event.EventPayload
import java.time.LocalDateTime

class ArticleLikedEventPayload @JsonCreator constructor(
    @JsonProperty("articleLikeId") val articleLikeId: Long,
    @JsonProperty("articleId") val articleId: Long,
    @JsonProperty("userId") val userId: Long,
    @JsonProperty("createdAt") val createdAt: LocalDateTime,
    @JsonProperty("articleLikeCount") val articleLikeCount: Long
): EventPayload