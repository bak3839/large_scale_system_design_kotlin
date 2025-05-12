package kuke.board.like.controller

import kuke.board.like.service.ArticleLikeService
import kuke.board.like.service.response.ArticleLikeResponse
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/article-likes")
class ArticleLikeController(
    private val articleService: ArticleLikeService,
) {
    @GetMapping("/articles/{articleId}/users/{userId}")
    fun read(
        @PathVariable("articleId") articleId: Long,
        @PathVariable("userId") userId: Long
    ): ArticleLikeResponse {
        return articleService.read(articleId, userId)
    }

    @PostMapping("/articles/{articleId}/users/{userId}")
    fun like(
        @PathVariable("articleId") articleId: Long,
        @PathVariable("userId") userId: Long,
    ) {
        articleService.like(articleId, userId)
    }

    @DeleteMapping("/articles/{articleId}/users/{userId}")
    fun unlike(
        @PathVariable("articleId") articleId: Long,
        @PathVariable("userId") userId: Long,
    ) {
        articleService.unlike(articleId, userId)
    }
}