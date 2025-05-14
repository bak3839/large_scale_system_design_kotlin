package kuke.board.article.repository

import kuke.board.article.entity.BoardArticleCount
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface BoardArticleCountRepository: JpaRepository<BoardArticleCount, Long> {
    @Query(
        value =
            """
                update board_article_count set article_count = article_count + 1 where board_id = :board_id
            """,
        nativeQuery = true
    )
    @Modifying
    fun increase(@Param("board_id") board_id: Long): Int

    @Query(
        value =
            """
                update board_article_count set article_count = article_count - 1 where board_id = :board_id
            """,
        nativeQuery = true
    )
    @Modifying
    fun decrease(@Param("board_id") board_id: Long): Int
}