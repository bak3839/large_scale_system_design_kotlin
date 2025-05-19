package kuke.board.hotarticle.service

import kuke.board.hotarticle.repository.ArticleCommentCountRepository
import kuke.board.hotarticle.repository.ArticleLikeCountRepository
import kuke.board.hotarticle.repository.ArticleViewCountRepository
import org.springframework.stereotype.Component

@Component
class HotArticleScoreCalculator(
    private val articleLikeCountRepository: ArticleLikeCountRepository,
    private val articleViewCountRepository: ArticleViewCountRepository,
    private val articleCommentCountRepository: ArticleCommentCountRepository,
) {
    companion object {
        private val ARTICLE_LIKE_COUNT_WEIGHT = 3L
        private val ARTICLE_COMMENT_COUNT_WEIGHT = 2L
        private val ARTICLE_VIEW_COUNT_WEIGHT = 1L
    }

    fun calculate(articleId: Long): Long {
        val articleLikeCount = articleLikeCountRepository.read(articleId)
        val articleViewCount = articleViewCountRepository.read(articleId)
        val articleCommentCount = articleCommentCountRepository.read(articleId)

        return (articleLikeCount * ARTICLE_LIKE_COUNT_WEIGHT
                + articleViewCount * ARTICLE_VIEW_COUNT_WEIGHT
                + articleCommentCount * ARTICLE_COMMENT_COUNT_WEIGHT
                )
    }
}