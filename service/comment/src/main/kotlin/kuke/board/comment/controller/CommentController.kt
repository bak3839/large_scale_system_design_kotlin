package kuke.board.comment.controller

import kuke.board.comment.service.CommentService
import kuke.board.comment.service.request.CommentCreateRequest
import kuke.board.comment.service.response.CommentPageResponse
import kuke.board.comment.service.response.CommentResponse
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/v1/comments")
class CommentController(
    private val commentService: CommentService,
) {
    @GetMapping("/{commentId}")
    fun read(
        @PathVariable("commentId") commentId: Long
    ): CommentResponse {
        return commentService.read(commentId)
    }

    @PostMapping("/create")
    fun create(@RequestBody request: CommentCreateRequest): CommentResponse {
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
        @RequestParam(value = "lastParentCommentId", required = false) lastParentCommentId: Long?,
        @RequestParam(value = "lastCommentId", required = false) lastCommentId: Long?,
        @RequestParam("pageSize") pageSize: Long
    ): List<CommentResponse> {
        return commentService.readAllInfiniteScroll(articleId, lastParentCommentId, lastCommentId, pageSize)
    }
}