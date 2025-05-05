package kuke.board.article.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Table(name = "article")
@Entity
class Article(
    @Id
    val articleId: Long,
    var title: String,
    var content: String,
    val boardId: Long, // shard key
    val writerId: Long,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    var modifiedAt: LocalDateTime = LocalDateTime.now(),
) {
    override fun toString(): String {
        return "Article(articleId= $articleId, title= $title ,content= $content ,boardId= $boardId, writerId= $writerId , createdAt= $createdAt, modifiedAt= $modifiedAt)"
    }
    fun update(title: String, content: String) {
        this.title = title
        this.content = content
        this.modifiedAt = LocalDateTime.now()
    }
}

