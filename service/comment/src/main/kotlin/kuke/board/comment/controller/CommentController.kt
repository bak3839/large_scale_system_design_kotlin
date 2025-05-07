package kuke.board.comment.controller

import kuke.board.comment.service.CommentService
import kuke.board.comment.service.request.CommentCreateRequest
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
}