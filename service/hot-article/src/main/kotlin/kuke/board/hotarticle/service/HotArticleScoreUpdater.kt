package kuke.board.hotarticle.service

import kuke.board.common.event.Event
import kuke.board.common.event.EventPayload
import kuke.board.hotarticle.repository.ArticleCreatedTimeRepository
import kuke.board.hotarticle.repository.HotArticleListRepository
import kuke.board.hotarticle.service.eventhandler.EventHandler
import org.springframework.stereotype.Component
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime

@Component
class HotArticleScoreUpdater(
    private val hotArticleListRepository: HotArticleListRepository,
    private val hotArticleScoreCalculator: HotArticleScoreCalculator,
    private val articleCreatedTimeRepository: ArticleCreatedTimeRepository
) {
    companion object {
        private val HOT_ARTICLE_COUNT = 10L
        private val HOT_ARTICLE_TTL = Duration.ofDays(10)
    }

    private fun isArticleCreatedToday(createdTime: LocalDateTime): Boolean
    = createdTime.toLocalDate().equals(LocalDate.now())

    fun update(event: Event<EventPayload>, eventHandler: EventHandler<EventPayload>) {
        val articleId = eventHandler.findArticleId(event)
        val createdTime = articleCreatedTimeRepository.read(articleId) ?: return

        // 오늘 작성한 게시글인지 판별
        if(!isArticleCreatedToday(createdTime)) return

        eventHandler.handle(event)

        val score = hotArticleScoreCalculator.calculate(articleId)
        hotArticleListRepository.add(
            articleId = articleId,
            time = createdTime,
            score = score,
            limit = HOT_ARTICLE_COUNT,
            ttl = HOT_ARTICLE_TTL
        )
    }
}