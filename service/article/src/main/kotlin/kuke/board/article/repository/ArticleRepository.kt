package kuke.board.article.repository

import kuke.board.article.entity.Article
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

interface ArticleRepository: JpaRepository<Article, Long> {
    @Query(
        value = "select article.article_id, article.title, article.content, article.board_id, article.writer_id, " +
                "article.created_at, article.modified_at " +
                "from (" +
                "   select article_id from article " +
                "   where board_id = :boardId " +
                "   order by article_id desc " +
                "   limit :limit offset :offset " +
                ") t left join article on t.article_id = article.article_id ",
        nativeQuery = true
    )
    fun findAll(
        @Param("boardId") boardId: Long,
        @Param("offset") offset: Long,
        @Param("limit") limit: Long
    ): List<Article>


    @Query(
        value =
            """
                select count(*) from (
                    select article_id from article 
                    where board_id = :boardId
                    limit :limit
                ) t
            """,
        nativeQuery = true
    )
    fun count(@Param("boardId") boardId: Long, @Param("limit") limit: Long): Long

    @Query(
        value =
            """
                select article.article_id, article.title, article.content, article.board_id, article.writer_id, 
                article.created_at, article.modified_at 
                from article
                where board_id = :boardId
                order by article_id desc limit :limit
            """,
        nativeQuery = true
    )
    fun findAllInfiniteScroll(
        @Param("boardId") boardId: Long,
        @Param("limit") limit: Long
    ): List<Article>

    @Query(
        value =
            """
                select article.article_id, article.title, article.content, article.board_id, article.writer_id, 
                article.created_at, article.modified_at 
                from article
                where board_id = :boardId and article_id < :lastArticleId
                order by article_id desc limit :limit
            """,
        nativeQuery = true
    )
    fun findAllInfiniteScroll(
        @Param("boardId") boardId: Long,
        @Param("limit") limit: Long,
        @Param("lastArticleId") lastArticleId: Long?
    ): List<Article>
}