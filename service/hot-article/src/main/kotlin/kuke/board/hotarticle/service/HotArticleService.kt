package kuke.board.hotarticle.service

import kuke.board.common.event.Event
import kuke.board.common.event.EventPayload
import kuke.board.common.event.EventType
import kuke.board.hotarticle.client.ArticleClient
import kuke.board.hotarticle.repository.HotArticleListRepository
import kuke.board.hotarticle.service.eventhandler.EventHandler
import org.springframework.stereotype.Service

@Service
class HotArticleService(
    private val articleClient: ArticleClient,
    private val eventHandlers: List<EventHandler<EventPayload>>,
    private val hotArticleScoreUpdater: HotArticleScoreUpdater,
    private val hotArticleListRepository: HotArticleListRepository
) {
    fun handleEvent(event: Event<EventPayload>) {
        val eventHandler = findEventHandler(event) ?: return

        if(isArticleCreatedOrDeleted(event)) {
            eventHandler.handle(event)
        } else {
            hotArticleScoreUpdater.update(event, eventHandler)
        }
    }

    private fun findEventHandler(event: Event<EventPayload>): EventHandler<EventPayload>?
    = eventHandlers.firstOrNull { it.supports(event) }

    private fun isArticleCreatedOrDeleted(event: Event<EventPayload>): Boolean
    = EventType.ARTICLE_CREATED == event.type || EventType.ARTICLE_DELETED == event.type
}