package kuke.board.like.repository

import jakarta.persistence.LockModeType
import kuke.board.like.entity.ArticleLikeCount
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface ArticleLikeCountRepository : JpaRepository<ArticleLikeCount, Long> {

    fun findByArticleId(articleId: Long): ArticleLikeCount?
    // select ... for update
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    fun findLockedByArticleId(articleId: Long): ArticleLikeCount?

    @Query(
        value =
            """
                update article_like_count set like_count = like_count + 1 where article_id = :articleId
            """,
        nativeQuery = true
    )
    @Modifying
    fun increase(@Param("articleId") articleId: Long): Int

    @Query(
        value =
            """
                update article_like_count set like_count = like_count - 1 where article_id = :articleId
            """,
        nativeQuery = true
    )
    @Modifying
    fun decrease(@Param("articleId") articleId: Long): Int
}