package kuke.board.articleread.service.response

import kuke.board.articleread.repository.ArticleQueryModel
import java.time.LocalDateTime

data class ArticleReadResponse(
    val articleId: Long,
    val title: String,
    val content: String,
    val boardId: Long,
    val writerId: Long,
    val createdAt: LocalDateTime,
    val modifiedAt: LocalDateTime,
    val articleCommentCount: Long,
    val articleLikeCount: Long,
) {
    companion object {
        fun from(articleQueryModel: ArticleQueryModel, viewCount: Long): ArticleReadResponse
        = ArticleReadResponse(
            articleId = articleQueryModel.articleId,
            title = articleQueryModel.title,
            content = articleQueryModel.content,
            boardId = articleQueryModel.boardId,
            writerId = articleQueryModel.writerId,
            createdAt = articleQueryModel.createdAt,
            modifiedAt = articleQueryModel.modifiedAt,
            articleCommentCount = articleQueryModel.articleCommentCount,
            articleLikeCount = viewCount
        )
    }
}