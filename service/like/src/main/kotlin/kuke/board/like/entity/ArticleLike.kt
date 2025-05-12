package kuke.board.like.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Table(name = "article_like")
@Entity
class ArticleLike(
    @Id
    val articleLikeId: Long,
    val articleId: Long,
    val userId: Long,
    val createdAt: LocalDateTime = LocalDateTime.now(),
) {
    companion object {
        fun create(articleLikeId: Long, articleId: Long, userId: Long): ArticleLike {
            return ArticleLike(
                articleLikeId = articleLikeId,
                articleId = articleId,
                userId = userId
            )
        }
    }
}