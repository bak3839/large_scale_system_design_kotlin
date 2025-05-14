package kuke.board.comment.controller

import kuke.board.comment.service.CommentService
import kuke.board.comment.service.CommentServiceV2
import kuke.board.comment.service.request.CommentCreateRequest
import kuke.board.comment.service.request.CommentCreateRequestV2
import kuke.board.comment.service.response.CommentPageResponse
import kuke.board.comment.service.response.CommentResponse
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/v2/comments")
class CommentControllerV2(
    private val commentService: CommentServiceV2,
) {
    @GetMapping("/{commentId}")
    fun read(
        @PathVariable("commentId") commentId: Long
    ): CommentResponse {
        return commentService.read(commentId)
    }

    @PostMapping("/create")
    fun create(@RequestBody request: CommentCreateRequestV2): CommentResponse {
        return commentService.create(request)
    }

    @DeleteMapping("/delete/{commentId}")
    fun delete(@PathVariable("commentId") commentId: Long) {
        commentService.delete(commentId)
    }

    @GetMapping("/readAll")
    fun read(
        @RequestParam("articleId") articleId: Long,
        @RequestParam("pageSize") pageSize: Long,
        @RequestParam("page") page: Long,
    ): CommentPageResponse {
        return commentService.readAll(articleId, page, pageSize)
    }

    @GetMapping("/readAll/infinite-scroll")
    fun readInfiniteScroll(
        @RequestParam("articleId") articleId: Long,
        @RequestParam(value = "lastPath", required = false) lastPath: String?,
        @RequestParam("pageSize") pageSize: Long
    ): List<CommentResponse> {
        return commentService.readAllInfiniteScroll(articleId, lastPath, pageSize)
    }

    @GetMapping("/articles/{articleId}/count")
    fun count(
        @PathVariable("articleId") articleId: Long
    ): Long {
        return commentService.count(articleId)
    }
}