package kuke.board.hotarticle.service

import kuke.board.common.event.Event
import kuke.board.common.event.EventPayload
import kuke.board.common.event.EventType
import kuke.board.hotarticle.client.ArticleClient
import kuke.board.hotarticle.repository.HotArticleListRepository
import kuke.board.hotarticle.service.eventhandler.EventHandler
import kuke.board.hotarticle.service.response.HotArticleResponse
import mu.KotlinLogging
import org.springframework.stereotype.Service
import java.util.Objects
import java.util.Objects.nonNull

@Service
class HotArticleService(
    private val articleClient: ArticleClient,
    private val eventHandlers: List<EventHandler<*>>,
    private val hotArticleScoreUpdater: HotArticleScoreUpdater,
    private val hotArticleListRepository: HotArticleListRepository
) {
    private val log = KotlinLogging.logger {}

    fun handleEvent(event: Event<EventPayload>) {
        @Suppress("UNCHECKED_CAST")
        val eventHandler = findEventHandler(event) as EventHandler<EventPayload>?

        if(eventHandler == null) {
            log.error { "[HotArticleService.handelEvent] Event handler not found" }
            log.error { "[HotArticleService.handelEvent] eventHandlers.size=${eventHandlers.size}" }
            return
        }
        log.info { "[HotArticleService.handelEvent] Event handler found" }
        if(isArticleCreatedOrDeleted(event)) {
            eventHandler.handle(event)
        } else {
            hotArticleScoreUpdater.update(event, eventHandler)
        }
    }

    private fun findEventHandler(event: Event<EventPayload>): EventHandler<*>? {
        @Suppress("UNCHECKED_CAST")
        eventHandlers as List<EventHandler<EventPayload>>
        return eventHandlers.stream()
            .filter { it.supports(event) }
            .findAny()
            .orElse(null);
    }

    private fun isArticleCreatedOrDeleted(event: Event<EventPayload>): Boolean
    = EventType.ARTICLE_CREATED == event.type || EventType.ARTICLE_DELETED == event.type

    fun readAll(dateStr: String): List<HotArticleResponse> {
        val result = hotArticleListRepository.readAll(dateStr) ?: return emptyList()
        return result
            .mapNotNull(articleClient::read) // 원본 데이터 가져오기
            .map(HotArticleResponse::from)
    }
}