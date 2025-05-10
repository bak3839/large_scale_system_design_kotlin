package kuke.board.comment.entity

import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Table(name = "comment_v2")
@Entity
class CommentV2(
    @Id
    val commentId : Long,
    var content : String,
    val articleId : Long,
    val writerId : Long,
    @Embedded
    var commentPath: CommentPath,
    var deleted : Boolean = false,
    val createdAt : LocalDateTime = LocalDateTime.now()
) {
    companion object {
        fun create(commentId: Long, content: String, articleId: Long, writerId: Long, commentPath: CommentPath): CommentV2 {
            return CommentV2(commentId, content, articleId, writerId, commentPath)
        }
    }

    fun isRoot(): Boolean = commentPath.isRoot()
    fun delete() {
        deleted = true
    }
}