package kuke.board.comment.service

import kuke.board.comment.entity.Comment
import kuke.board.comment.repository.CommentRepository
import kuke.board.comment.service.request.CommentCreateRequest
import kuke.board.comment.service.response.CommentResponse
import kuke.board.common.snowflake.Snowflake
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.function.Predicate

@Service
class CommentService(
    private val commentRepository: CommentRepository,
) {
    private val snowflake = Snowflake()

    @Transactional
    fun create(request: CommentCreateRequest): CommentResponse {
        val parent = findParent(request)

        val comment = commentRepository.save(
            Comment.create(
                commentId = snowflake.nextId(),
                content = request.content,
                parentCommentId = parent?.commentId,
                articleId = request.articleId,
                writerId = request.writerId,
            )
        )

        return CommentResponse.from(comment)
    }

    private fun findParent(request: CommentCreateRequest): Comment? {
        val parentCommentId = request.parentCommentId ?: return null

        return commentRepository.findById(parentCommentId)
            .filter(Predicate.not(Comment::deleted))
            .filter(Comment::isRoot)
            .orElseThrow()
    }

    fun read(commentId: Long): CommentResponse {
        return CommentResponse.from(
            commentRepository.findById(commentId).orElseThrow()
        )
    }

    @Transactional
    fun delete(commentId: Long) {
        commentRepository.findById(commentId)
            .filter(Predicate.not(Comment::deleted))
            .ifPresent {
                when(hasChildren(it)) {
                    true -> it.delete()
                    false -> delete(it)
                }
            }
    }

    private fun hasChildren(comment: Comment): Boolean
    = commentRepository.countBy(comment.articleId, comment.parentCommentId, 2L) == 2L

    private fun delete(comment: Comment) {
        commentRepository.delete(comment)

        // 상위 댓글을 검사해서 deleted = true 이면 삭제 진행
        if(!comment.isRoot()) {
            commentRepository.findById(comment.parentCommentId)
                .filter(Comment::deleted)
                .filter(Predicate.not(this::hasChildren))
                .ifPresent(this::delete)
        }
    }
}