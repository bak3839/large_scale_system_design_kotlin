package kuke.board.comment.repository

import kuke.board.comment.entity.Comment
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface CommentRepository: JpaRepository<Comment, Long> {
    fun findByCommentId(commentId: Long): Comment?

    @Query(
        value =
            """
                select count(*) from (
                    select comment_id from comment
                    where article_id = :articleId and parent_comment_id = :parentCommentId
                    limit = :limit
                ) t
            """,
        nativeQuery = true
    )
    fun countBy(
        @Param("articleId") articleId: Long,
        @Param("parentCommentId") parentCommentId: Long,
        @Param("limit") limit: Long
    ): Long
}