package kuke.board.comment.service.response

data class CommentPageResponse(
    val comments: List<CommentResponse>,
    val commentCount: Long
) {
    companion object {
        fun of(comments: List<CommentResponse>, commentCount: Long): CommentPageResponse
        = CommentPageResponse(comments, commentCount)
    }
}