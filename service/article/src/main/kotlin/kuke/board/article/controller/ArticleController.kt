package kuke.board.article.controller

import kuke.board.article.entity.Article
import kuke.board.article.service.ArticleService
import kuke.board.article.service.request.ArticleCreateRequest
import kuke.board.article.service.request.ArticleUpdateRequest
import kuke.board.article.service.response.ArticleResponse
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/v1/articles")
class ArticleController(
    private val articleService: ArticleService,
) {
    @GetMapping("/{articleId}")
    fun read(@PathVariable articleId: Long): ArticleResponse {
        return articleService.read(articleId)
    }

    @PostMapping("/create")
    fun create(@RequestBody request: ArticleCreateRequest): ArticleResponse {
        return articleService.create(request)
    }

    @PutMapping("/{articleId}/update")
    fun update(
        @PathVariable articleId: Long,
        @RequestBody request: ArticleUpdateRequest
    ): ArticleResponse {
        return articleService.update(articleId, request)
    }

    @DeleteMapping("/{articleId}/delete")
    fun delete(@PathVariable articleId: Long) {
        articleService.delete(articleId)
    }
}