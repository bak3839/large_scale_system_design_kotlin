package kuke.board.like.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.Version

@Entity
@Table(name = "article_like_count")
class ArticleLikeCount(
    @Id
    val articleId: Long,
    var likeCount: Long,
    @Version
    var version: Long = 0L
) {
    companion object {
        fun init(articleId: Long, likeCount: Long): ArticleLikeCount {
            return ArticleLikeCount(
                articleId = articleId,
                likeCount = likeCount
            )
        }
    }

    fun increase() { this.likeCount++ }
    fun decrease() { this.likeCount-- }
}