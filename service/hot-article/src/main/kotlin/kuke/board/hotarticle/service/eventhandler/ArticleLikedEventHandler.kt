package kuke.board.hotarticle.service.eventhandler

import kuke.board.common.event.Event
import kuke.board.common.event.EventType
import kuke.board.common.event.payload.ArticleLikedEventPayload
import kuke.board.hotarticle.repository.ArticleLikeCountRepository
import kuke.board.hotarticle.utils.TimeCalculatorUtils

class ArticleLikedEventHandler(
    private val articleLikeCountRepository: ArticleLikeCountRepository
): EventHandler<ArticleLikedEventPayload> {
    override fun handle(event: Event<ArticleLikedEventPayload>) {
        val payload = event.payload
        articleLikeCountRepository.createOrUpdate(
            articleId = payload.articleId,
            likeCount = payload.articleLikeCount,
            ttl = TimeCalculatorUtils.calculateDurationToMidnight()
        )
    }

    override fun supports(event: Event<ArticleLikedEventPayload>): Boolean
    = EventType.ARTICLE_LIKED == event.type

    override fun findArticleId(event: Event<ArticleLikedEventPayload>): Long
    = event.payload.articleId
}