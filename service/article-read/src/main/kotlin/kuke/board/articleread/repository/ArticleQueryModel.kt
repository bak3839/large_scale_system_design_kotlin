package kuke.board.articleread.repository

import kuke.board.articleread.client.ArticleClient
import kuke.board.common.event.payload.*
import java.time.LocalDateTime

class ArticleQueryModel(
    var articleId: Long,
    var title: String,
    var content: String,
    var boardId: Long,
    var writerId: Long,
    var createdAt: LocalDateTime,
    var modifiedAt: LocalDateTime,
    var articleCommentCount: Long,
    var articleLikeCount: Long,
) {
    companion object {
        fun create(payload: ArticleCreatedEventPayload): ArticleQueryModel {
            return ArticleQueryModel(
                articleId = payload.articleId,
                title = payload.title,
                content = payload.content,
                boardId = payload.boardId,
                writerId = payload.writerId,
                createdAt = payload.createdAt,
                modifiedAt = payload.modifiedAt,
                articleCommentCount = 0L,
                articleLikeCount = 0L
            )
        }

        fun create(
            likeCount: Long,
            commentCount: Long,
            article: ArticleClient.Companion.ArticleResponse
        ): ArticleQueryModel {
            return ArticleQueryModel(
                articleId = article.articleId,
                title = article.title,
                content = article.content,
                boardId = article.boardId,
                writerId = article.writerId,
                createdAt = article.createdAt,
                modifiedAt = article.modifiedAt,
                articleCommentCount = commentCount,
                articleLikeCount = likeCount
            )
        }
    }

    fun updateBy(payload: ArticleUpdatedEventPayload) {
        this.title = payload.title
        this.content = payload.content
        this.boardId = payload.boardId
        this.writerId = payload.writerId
        this.createdAt = payload.createdAt
        this.modifiedAt = payload.modifiedAt
    }

    fun updateBy(payload: CommentCreatedEventPayload) {
        this.articleCommentCount = payload.articleCommentCount
    }

    fun updateBy(payload: CommentDeletedEventPayload) {
        this.articleCommentCount = payload.articleCommentCount
    }

    fun updateBy(payload: ArticleLikedEventPayload) {
        this.articleLikeCount = payload.articleLikeCount
    }

    fun updateBy(payload: ArticleUnlikedEventPayload) {
        this.articleLikeCount = payload.articleLikeCount
    }
}