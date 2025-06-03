package kuke.board.articleread.service.event.handler

import kuke.board.articleread.repository.ArticleIdListRepository
import kuke.board.articleread.repository.ArticleQueryModel
import kuke.board.articleread.repository.ArticleQueryModelRepository
import kuke.board.articleread.repository.BoardArticleCountRepository
import kuke.board.common.event.Event
import kuke.board.common.event.EventType
import kuke.board.common.event.payload.ArticleCreatedEventPayload
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class ArticleCreatedEventHandler(
    private val articleQueryModelRepository: ArticleQueryModelRepository,
    private val articleIdListRepository: ArticleIdListRepository,
    private val boardArticleCountRepository: BoardArticleCountRepository
): EventHandler<ArticleCreatedEventPayload> {
    override fun handle(event: Event<ArticleCreatedEventPayload>) {
        val payload = event.payload
        articleQueryModelRepository.create(
            ArticleQueryModel.create(payload),
            Duration.ofDays(1)
        )
        articleIdListRepository.add(payload.boardId, payload.articleId, 1000L)
        boardArticleCountRepository.createOrUpdate(payload.boardId, payload.boardArticleCount)
    }

    override fun supports(event: Event<ArticleCreatedEventPayload>): Boolean
    = EventType.ARTICLE_CREATED == event.type
}