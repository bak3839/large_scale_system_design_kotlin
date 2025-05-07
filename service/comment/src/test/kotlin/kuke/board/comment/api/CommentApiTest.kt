package kuke.board.comment.api

import kuke.board.comment.service.response.CommentResponse
import org.junit.jupiter.api.Test
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestTemplate

class CommentApiTest {
    val restClient = RestClient.create("http://localhost:9001")

    @Test
    fun create() {
        val comment1 = CommentCreateRequest(1L, "comment1", null, 1L)
        val response1 = createComment(comment1)

        val comment2 = CommentCreateRequest(1L, "comment2", response1?.commentId, 1L)
        val response2 = createComment(comment2)

        val comment3 = CommentCreateRequest(1L, "comment3", response1?.commentId, 1L)
        val response3 = createComment(comment3)

        println("commentId: ${response1?.commentId}")
        println("commentId: ${response2?.commentId}")
        println("commentId: ${response3?.commentId}")

//        commentId: 178497063888166912
//        commentId: 178497065033211904
//        commentId: 178497065100320768
    }

    fun createComment(request: CommentCreateRequest): CommentResponse? {
        return restClient.post()
            .uri("/v1/comments/create")
            .body(request)
            .retrieve()
            .body(CommentResponse::class.java)
    }

    @Test
    fun read() {
        val response = restClient.get()
            .uri("/v1/comments/${178497063888166912}")
            .retrieve()
            .body(CommentResponse::class.java)

        println("response: $response")
    }

    @Test
    fun delete() {
        //        commentId: 178497063888166912 - X
        //         ㄴ commentId: 178497065033211904 - X
        //         ㄴ commentId: 178497065100320768 - X

        restClient.delete()
            .uri("/v1/comments/delete/${178497065100320768}")
            .retrieve()
    }

    companion object {
        data class CommentCreateRequest(
            val articleId: Long,
            val content: String,
            val parentCommentId: Long?,
            val writerId: Long,
        )
    }
}

