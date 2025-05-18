package kuke.board.common.event.payload

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import kuke.board.common.event.EventPayload
import java.time.LocalDateTime


class ArticleCreatedEventPayload @JsonCreator constructor(
    @JsonProperty("articleId") val articleId: Long,
    @JsonProperty("title") val title: String,
    @JsonProperty("content") val content: String,
    @JsonProperty("boardId") val boardId: Long,
    @JsonProperty("writerId") val writerId: Long,
    @JsonProperty("createdAt") val createdAt: LocalDateTime,
    @JsonProperty("modifiedAt") val modifiedAt: LocalDateTime,
    @JsonProperty("boardArticleCount") val boardArticleCount: Long
) : EventPayload