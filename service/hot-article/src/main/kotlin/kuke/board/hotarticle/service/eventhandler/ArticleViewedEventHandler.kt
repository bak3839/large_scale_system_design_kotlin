package kuke.board.hotarticle.service.eventhandler

import kuke.board.common.event.Event
import kuke.board.common.event.EventType
import kuke.board.common.event.payload.ArticleViewedEventPayload
import kuke.board.hotarticle.repository.ArticleViewCountRepository
import kuke.board.hotarticle.utils.TimeCalculatorUtils

class ArticleViewedEventHandler(
    private val articleViewCountRepository: ArticleViewCountRepository
): EventHandler<ArticleViewedEventPayload> {
    override fun handle(event: Event<ArticleViewedEventPayload>) {
        val payload = event.payload
        articleViewCountRepository.createOrUpdate(
            articleId = payload.articleId,
            viewCount = payload.articleViewCount,
            ttl = TimeCalculatorUtils.calculateDurationToMidnight()
        )
    }

    override fun supports(event: Event<ArticleViewedEventPayload>): Boolean
    = EventType.ARTICLE_VIEWED == event.type

    override fun findArticleId(event: Event<ArticleViewedEventPayload>): Long
    = event.payload.articleId
}