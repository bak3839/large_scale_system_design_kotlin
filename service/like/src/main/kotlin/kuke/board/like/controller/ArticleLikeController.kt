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

    @GetMapping("/articles/{articleId}/count")
    fun count(
        @PathVariable("articleId") articleId: Long
    ): Long {
        return articleService.count(articleId)
    }

    @PostMapping("/articles/{articleId}/users/{userId}/pessimistic-lock1")
    fun likePessimisticLock1(
        @PathVariable("articleId") articleId: Long,
        @PathVariable("userId") userId: Long,
    ) {
        articleService.likePessimisticLock1(articleId, userId)
    }

    @DeleteMapping("/articles/{articleId}/users/{userId}/pessimistic-lock1")
    fun unlikePessimisticLock1(
        @PathVariable("articleId") articleId: Long,
        @PathVariable("userId") userId: Long,
    ) {
        articleService.unlikePessimisticLock1(articleId, userId)
    }

    @PostMapping("/articles/{articleId}/users/{userId}/pessimistic-lock2")
    fun likePessimisticLock2(
        @PathVariable("articleId") articleId: Long,
        @PathVariable("userId") userId: Long,
    ) {
        articleService.likePessimisticLock2(articleId, userId)
    }

    @DeleteMapping("/articles/{articleId}/users/{userId}/pessimistic-lock2")
    fun unlikePessimisticLock2(
        @PathVariable("articleId") articleId: Long,
        @PathVariable("userId") userId: Long,
    ) {
        articleService.unlikePessimisticLock2(articleId, userId)
    }

    @PostMapping("/articles/{articleId}/users/{userId}/optimistic-lock")
    fun likeOptimisticLock(
        @PathVariable("articleId") articleId: Long,
        @PathVariable("userId") userId: Long,
    ) {
        articleService.likePessimisticLock2(articleId, userId)
    }

    @DeleteMapping("/articles/{articleId}/users/{userId}/optimistic-lock")
    fun unlikeOptimisticLock(
        @PathVariable("articleId") articleId: Long,
        @PathVariable("userId") userId: Long,
    ) {
        articleService.unlikePessimisticLock2(articleId, userId)
    }
}