package kuke.board.hotarticle.service

import io.kotest.matchers.ints.exactly
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import kuke.board.common.event.Event
import kuke.board.common.event.EventPayload
import kuke.board.common.event.EventType
import kuke.board.hotarticle.client.ArticleClient
import kuke.board.hotarticle.repository.HotArticleListRepository
import kuke.board.hotarticle.service.eventhandler.ArticleCreatedEventHandler
import kuke.board.hotarticle.service.eventhandler.EventHandler
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.web.client.RestClient
import java.util.stream.Stream

@ExtendWith(MockKExtension::class)
class HotArticleServiceTest {
    @RelaxedMockK
    lateinit var eventHandlers: List<EventHandler<EventPayload>>

    @RelaxedMockK
    lateinit var hotArticleScoreUpdater: HotArticleScoreUpdater

    @MockK
    lateinit var articleClient: ArticleClient

    @MockK
    lateinit var hotArticleListRepository: HotArticleListRepository

    @InjectMockKs
    lateinit var hotArticleService: HotArticleService

    @Test
    fun `이벤트 핸들러를 찾지 못하면 기능을 수행하지 않음`() {
        val event = mockk<Event<EventPayload>>()
        val eventHandler = mockk<EventHandler<EventPayload>>(relaxed = true)

        every { eventHandler.supports(event) } returns false
        every { eventHandlers.stream() } returns Stream.of(eventHandler)

        // when
        hotArticleService.handleEvent(event)

        // then
        verify(exactly = 0) { eventHandler.handle(event) }
        verify(exactly = 0) { hotArticleScoreUpdater.update(event, eventHandler) }
    }

    @Test
    fun `게시글 생성 시 이벤트 핸들러를 찾으면 기능을 수행`() {
        val event = mockk<Event<EventPayload>>()
        val eventHandler = mockk<EventHandler<EventPayload>>(relaxed = true)

        every { event.type } returns EventType.ARTICLE_CREATED
        every { eventHandler.supports(event) } returns true
        every { eventHandlers.stream() } returns Stream.of(eventHandler)

        // when
        hotArticleService.handleEvent(event)

        // then
        verify(exactly = 1) { eventHandler.handle(event) }
        verify(exactly = 0) { hotArticleScoreUpdater.update(event, eventHandler) }
    }

    @Test
    fun `게시글 삭제 시 이벤트 핸들러를 찾으면 기능을 수행`() {
        val event = mockk<Event<EventPayload>>()
        val eventHandler = mockk<EventHandler<EventPayload>>(relaxed = true)

        every { event.type } returns EventType.ARTICLE_DELETED
        every { eventHandler.supports(event) } returns true
        every { eventHandlers.stream() } returns Stream.of(eventHandler)

        // when
        hotArticleService.handleEvent(event)

        // then
        verify(exactly = 1) { eventHandler.handle(event) }
        verify(exactly = 0) { hotArticleScoreUpdater.update(event, eventHandler) }
    }

    @Test
    fun `점수가 업데이트 되어야 하는 경우`() {
        val event = mockk<Event<EventPayload>>()
        val eventHandler = mockk<EventHandler<EventPayload>>(relaxed = true)

        every { event.type } returns EventType.ARTICLE_LIKED
        every { eventHandler.supports(event) } returns true
        every { eventHandlers.stream() } returns Stream.of(eventHandler)

        // when
        hotArticleService.handleEvent(event)

        // then
        verify(exactly = 0) { eventHandler.handle(event) }
        verify(exactly = 1) { hotArticleScoreUpdater.update(event, eventHandler) }
    }
}