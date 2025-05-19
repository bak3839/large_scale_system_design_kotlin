package kuke.board.hotarticle.service.eventhandler

import kuke.board.common.event.Event
import kuke.board.common.event.EventType
import kuke.board.common.event.payload.ArticleDeletedEventPayload
import kuke.board.hotarticle.repository.ArticleCreatedTimeRepository
import kuke.board.hotarticle.repository.HotArticleListRepository
import org.springframework.stereotype.Component

@Component
class ArticleDeletedEventHandler(
    private val hotArticleListRepository: HotArticleListRepository,
    private val articleCreatedTimeRepository: ArticleCreatedTimeRepository
): EventHandler<ArticleDeletedEventPayload> {

    override fun handle(event: Event<ArticleDeletedEventPayload>) {
        val payload = event.payload
        articleCreatedTimeRepository.delete(payload.articleId)
        hotArticleListRepository.remove(payload.articleId, payload.createdAt)
    }

    override fun supports(event: Event<ArticleDeletedEventPayload>): Boolean
    = EventType.ARTICLE_DELETED == event.type

    override fun findArticleId(event: Event<ArticleDeletedEventPayload>): Long
    = event.payload.articleId
}