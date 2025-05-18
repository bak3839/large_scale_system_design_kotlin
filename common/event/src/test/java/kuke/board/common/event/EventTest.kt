package kuke.board.common.event

import kuke.board.common.event.payload.ArticleCreatedEventPayload
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class EventTest {
    @Test
    fun serde() {
        // given
        val payload = ArticleCreatedEventPayload(
            articleId = 1L,
            title = "title",
            content = "content",
            createdAt = LocalDateTime.now(),
            modifiedAt = LocalDateTime.now(),
            boardId = 1L,
            writerId = 1L,
            boardArticleCount = 23L
        )

        val event = Event.of(
            eventId = 1234L,
            type = EventType.ARTICLE_CREATED,
            eventPayload = payload
        )

        val json = event.toJson()
        println("json: $json")

        // when
        val result = Event.fromJson(json)

        // then
        assertNotNull(result)
        result?.also {
            assertEquals(event.eventId, it.eventId)
            assertEquals(event.type, it.type)
            assertInstanceOf(payload.javaClass, it.payload)
        }

        val resultPayload = result?.payload as ArticleCreatedEventPayload?
        
        assertNotNull(resultPayload)
        resultPayload?.also {
            assertEquals(it.articleId, payload.articleId)
            assertEquals(it.title, payload.title)
            assertEquals(it.createdAt, payload.createdAt)
        }
    }
}