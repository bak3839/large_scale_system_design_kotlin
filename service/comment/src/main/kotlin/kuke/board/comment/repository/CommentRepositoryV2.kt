package kuke.board.comment.repository

import kuke.board.comment.entity.CommentV2
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.Optional

interface CommentRepositoryV2 : JpaRepository<CommentV2, Long> {

    @Query("select c from CommentV2 c where c.commentPath = :path")
    fun findByPath(
        @Param("path") path: String,
    ): Optional<CommentV2>

    @Query(
        value =
            """
                select path from comment_v2
                where article_id = :articleId and path > :pathPrefix and path like :pathPrefix
                order by path desc limit 1
            """,
        nativeQuery = true
    )
    fun findDescendantsTopPath(
        @Param("articleId") articleId: Long,
        @Param("pathPrefix") pathPrefix: String,
    ): String?
}