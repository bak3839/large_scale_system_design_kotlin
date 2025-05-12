package kuke.board.like.repository

import kuke.board.like.entity.ArticleLike
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface ArticleLikeRepository: JpaRepository<ArticleLike, Long> {
    fun findByArticleIdAndUserId(articleId: Long, userId: Long): ArticleLike?
}