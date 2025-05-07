package kuke.board.comment.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Table(name = "comment")
@Entity
class Comment(
    @Id
    val commentId : Long,
    var content : String,
    var parentCommentId : Long,
    val articleId : Long,
    val writerId : Long,
    var deleted : Boolean = false,
    val createdAt : LocalDateTime = LocalDateTime.now()
) {
    companion object {
        fun create(
            commentId: Long,
            content: String,
            parentCommentId: Long?,
            articleId: Long,
            writerId: Long
        ): Comment {
            val parentId = parentCommentId ?: commentId
            return Comment(commentId, content, parentId, articleId, writerId)
        }
    }

    fun isRoot() = parentCommentId == commentId
    fun delete() {
        deleted = true
    }
}