package kuke.board.like.service.response

import jakarta.persistence.Id
import kuke.board.like.entity.ArticleLike
import java.time.LocalDateTime

data class ArticleLikeResponse(
    val articleLikeId: Long,
    val articleId: Long,
    val userId: Long,
    val createdAt: LocalDateTime,
) {
    companion object {
        fun from(articleLike: ArticleLike): ArticleLikeResponse {
            return ArticleLikeResponse(
                articleLikeId = articleLike.articleLikeId,
                articleId = articleLike.articleId,
                userId = articleLike.userId,
                createdAt = articleLike.createdAt
            )
        }
    }
}