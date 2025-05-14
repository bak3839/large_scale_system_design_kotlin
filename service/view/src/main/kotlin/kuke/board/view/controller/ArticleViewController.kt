package kuke.board.view.controller

import kuke.board.view.service.ArticleViewService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/article-views")
class ArticleViewController(
    private val articleViewService: ArticleViewService
) {
    @PostMapping("/articles/{articleId}/users/{userId}")
    fun increase(
        @PathVariable("articleId") articleId: Long,
        @PathVariable("userId") userId: Long
    ): Long?
    = articleViewService.increase(articleId, userId)

    @GetMapping("/articles/{articleId}/count")
    fun count(@PathVariable("articleId") articleId: Long): Long?
    = articleViewService.count(articleId)
}