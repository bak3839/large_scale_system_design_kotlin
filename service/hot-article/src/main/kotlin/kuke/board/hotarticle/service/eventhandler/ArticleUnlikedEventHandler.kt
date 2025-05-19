package kuke.board.hotarticle.service.eventhandler

import kuke.board.common.event.Event
import kuke.board.common.event.EventType
import kuke.board.common.event.payload.ArticleUnlikedEventPayload
import kuke.board.hotarticle.repository.ArticleLikeCountRepository
import kuke.board.hotarticle.utils.TimeCalculatorUtils

class ArticleUnlikedEventHandler(
    private val articleLikeCountRepository: ArticleLikeCountRepository
): EventHandler<ArticleUnlikedEventPayload> {
    override fun handle(event: Event<ArticleUnlikedEventPayload>) {
        val payload = event.payload
        articleLikeCountRepository.createOrUpdate(
            articleId = payload.articleId,
            likeCount = payload.articleLikeCount,
            ttl = TimeCalculatorUtils.calculateDurationToMidnight()
        )
    }

    override fun supports(event: Event<ArticleUnlikedEventPayload>): Boolean
    = EventType.ARTICLE_UNLIKED == event.type

    override fun findArticleId(event: Event<ArticleUnlikedEventPayload>): Long
    = event.payload.articleId
}