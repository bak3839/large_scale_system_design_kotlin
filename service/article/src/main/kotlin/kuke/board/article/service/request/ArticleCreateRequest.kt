package kuke.board.article.service.request

import java.time.LocalDateTime

data class ArticleCreateRequest(
    val title: String,
    val content: String,
    val boardId: Long,
    val writerId: Long
) {
}