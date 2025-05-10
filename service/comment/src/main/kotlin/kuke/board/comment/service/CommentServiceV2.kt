package kuke.board.comment.service

import kuke.board.comment.entity.CommentPath
import kuke.board.comment.entity.CommentV2
import kuke.board.comment.repository.CommentRepositoryV2
import kuke.board.comment.service.request.CommentCreateRequestV2
import kuke.board.comment.service.response.CommentResponse
import kuke.board.common.snowflake.Snowflake
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.function.Predicate

@Service
class CommentServiceV2(
    private val commentRepositoryV2: CommentRepositoryV2
) {
    private val snowflake = Snowflake()

    @Transactional
    fun create(request: CommentCreateRequestV2): CommentResponse {
        val parent: CommentV2? = findParent(request)
        val parentCommentPath = parent?.let {
            parent.commentPath
        } ?: CommentPath.create("")

        val comment = commentRepositoryV2.save(CommentV2.create(
            commentId = snowflake.nextId(),
            content = request.content,
            articleId = request.articleId,
            writerId = request.writerId,
            commentPath = parentCommentPath.createChildCommentPath(
                commentRepositoryV2.findDescendantsTopPath(request.articleId, parentCommentPath.path)
            )
        ))

        return CommentResponse.from(comment)
    }

    private fun findParent(request: CommentCreateRequestV2): CommentV2? {
        val parentPath = request.parentPath

        parentPath?.let {
            return commentRepositoryV2.findByPath(parentPath)
                .filter(Predicate.not(CommentV2::deleted))
                .orElseThrow()
        } ?: return null
    }
}