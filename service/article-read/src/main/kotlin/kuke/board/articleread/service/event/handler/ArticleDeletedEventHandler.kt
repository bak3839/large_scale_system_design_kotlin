package kuke.board.articleread.service.event.handler

import kuke.board.articleread.repository.ArticleIdListRepository
import kuke.board.articleread.repository.ArticleQueryModel
import kuke.board.articleread.repository.ArticleQueryModelRepository
import kuke.board.articleread.repository.BoardArticleCountRepository
import kuke.board.common.event.Event
import kuke.board.common.event.EventType
import kuke.board.common.event.payload.ArticleCreatedEventPayload
import kuke.board.common.event.payload.ArticleDeletedEventPayload
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class ArticleDeletedEventHandler(
    private val articleQueryModelRepository: ArticleQueryModelRepository,
    private val articleIdListRepository: ArticleIdListRepository,
    private val boardArticleCountRepository: BoardArticleCountRepository
): EventHandler<ArticleDeletedEventPayload> {
    override fun handle(event: Event<ArticleDeletedEventPayload>) {
        val payload = event.payload
        articleIdListRepository.delete(payload.boardId, payload.articleId)
        articleQueryModelRepository.delete(payload.articleId)
        boardArticleCountRepository.createOrUpdate(payload.boardId, payload.boardArticleCount)
    }

    override fun supports(event: Event<ArticleDeletedEventPayload>): Boolean
    = EventType.ARTICLE_DELETED == event.type
}