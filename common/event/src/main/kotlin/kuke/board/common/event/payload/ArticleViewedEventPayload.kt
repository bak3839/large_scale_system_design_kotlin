package kuke.board.common.event.payload

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import kuke.board.common.event.EventPayload

class ArticleViewedEventPayload @JsonCreator constructor(
    @JsonProperty("articleId") val articleId: Long,
    @JsonProperty("articleViewCount") val articleViewCount: Long,
): EventPayload {
}