package kuke.board.article.controller

import kuke.board.article.entity.Article
import kuke.board.article.service.ArticleService
import kuke.board.article.service.request.ArticleCreateRequest
import kuke.board.article.service.request.ArticleUpdateRequest
import kuke.board.article.service.response.ArticlePageResponse
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

    @GetMapping("/all")
    fun readAll(
        @RequestParam("boardId") boardId: Long,
        @RequestParam("page") page: Long,
        @RequestParam("pageSize") pageSize: Long,
    ): ArticlePageResponse {
        return articleService.readAll(boardId, page, pageSize)
    }

    @GetMapping("/infinite-scroll")
    fun readAllInfiniteScroll(
        @RequestParam("boardId") boardId: Long,
        @RequestParam("pageSize") pageSize: Long,
        @RequestParam(value = "lastArticleId", required = false) lastArticleId: Long?,
    ): List<ArticleResponse> {
        return articleService.readAllInfiniteScroll(boardId, pageSize, lastArticleId)
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

    @GetMapping("/boards/{boardId}/count")
    fun count(@PathVariable boardId: Long): Long {
        return articleService.count(boardId)
    }
}