package kuke.board.articleread.contorller

import kuke.board.articleread.service.ArticleReadService
import kuke.board.articleread.service.response.ArticleReadResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class ArticleReadController(
    private val articleReadService: ArticleReadService
) {
    @GetMapping("/v1/articles/{articleId}")
    fun articleRead(@PathVariable articleId: Long): ArticleReadResponse {
        return articleReadService.read(articleId)
    }
}