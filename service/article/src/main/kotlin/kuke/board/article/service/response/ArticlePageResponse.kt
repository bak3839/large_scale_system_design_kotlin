package kuke.board.article.service.response

import kuke.board.article.entity.Article

data class ArticlePageResponse(
    val articles: List<ArticleResponse>,
    val articleCount: Long
) {
    companion object {
        fun of(articles: List<ArticleResponse>, articleCount: Long): ArticlePageResponse {
            return ArticlePageResponse(
                articles = articles,
                articleCount = articleCount
            )
        }
    }
}

