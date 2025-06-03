package kuke.board.articleread.service.response


data class ArticleReadPageResponse(
    val articles: List<ArticleReadResponse>,
    val articleCount: Long
) {
    companion object {
        fun of(articles: List<ArticleReadResponse>, articleCount: Long): ArticleReadPageResponse
        = ArticleReadPageResponse(
            articles = articles,
            articleCount = articleCount
        )
    }
}