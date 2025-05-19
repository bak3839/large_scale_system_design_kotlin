package kuke.board.hotarticle.service

import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import kuke.board.common.event.Event
import kuke.board.common.event.EventPayload
import kuke.board.hotarticle.repository.ArticleCreatedTimeRepository
import kuke.board.hotarticle.repository.HotArticleListRepository
import kuke.board.hotarticle.service.eventhandler.EventHandler
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.LocalDateTime

@ExtendWith(MockKExtension::class)
class HotArticleScoreUpdaterTest {
    @RelaxedMockK
    lateinit var articleCreatedTimeRepository: ArticleCreatedTimeRepository

    @RelaxedMockK
    lateinit var hotArticleScoreCalculator: HotArticleScoreCalculator

    @RelaxedMockK
    lateinit var hotArticleListRepository: HotArticleListRepository

    @InjectMockKs
    lateinit var hotArticleScoreUpdater: HotArticleScoreUpdater

    @Test
    fun `오늘 생성된 게시글이 아니면 반영하지 않음`() {
        // given
        val articleId = 1L
        val event = mockk<Event<EventPayload>>()
        val eventHandler = mockk<EventHandler<EventPayload>>()
        val createdTime = LocalDateTime.now().minusDays(1)

        every { eventHandler.findArticleId(event) } returns articleId
        every { articleCreatedTimeRepository.read(articleId) } returns createdTime

        // when
        hotArticleScoreUpdater.update(event, eventHandler)

        // then
        verify(exactly = 0) { eventHandler.handle(event) }
        verify(exactly = 0) { hotArticleListRepository.add(any(), any(), any(), any(), any()) }
    }

    @Test
    fun `오늘 생성한 게시글이면 인기글에 반영`() {
        // given
        val articleId = 1L
        val event = mockk<Event<EventPayload>>()
        val eventHandler = mockk<EventHandler<EventPayload>>(relaxed = true)
        val createdTime = LocalDateTime.now()

        every { eventHandler.findArticleId(event) } returns articleId
        every { articleCreatedTimeRepository.read(articleId) } returns createdTime

        // when
        hotArticleScoreUpdater.update(event, eventHandler)

        // then
        verify(exactly = 1) { eventHandler.handle(any()) }
        verify(exactly = 1) { hotArticleListRepository.add(any(), any(), any(), any(), any()) }
    }
}