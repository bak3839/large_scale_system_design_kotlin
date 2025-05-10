package kuke.board.comment.service.response

import jakarta.persistence.Id
import kuke.board.comment.entity.Comment
import kuke.board.comment.entity.CommentV2
import java.time.LocalDateTime

data class CommentResponse(
    val commentId : Long,
    var content : String,
    var parentCommentId : Long? = null,
    val articleId : Long,
    val writerId : Long,
    var deleted : Boolean,
    val createdAt : LocalDateTime,
    var path: String? = null
) {
    companion object {
        fun from(comment: Comment): CommentResponse = CommentResponse(
            commentId = comment.commentId,
            content = comment.content,
            parentCommentId = comment.parentCommentId,
            articleId = comment.articleId,
            writerId = comment.writerId,
            deleted = comment.deleted,
            createdAt = comment.createdAt
        )

        fun from(comment: CommentV2): CommentResponse = CommentResponse(
            commentId = comment.commentId,
            content = comment.content,
            articleId = comment.articleId,
            writerId = comment.writerId,
            deleted = comment.deleted,
            createdAt = comment.createdAt,
            path = comment.commentPath.path
        )
    }
}