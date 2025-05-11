package kuke.board.comment.api

import kuke.board.comment.service.request.CommentCreateRequestV2
import kuke.board.comment.service.response.CommentResponse
import org.junit.jupiter.api.Test
import org.springframework.web.client.RestClient
import java.time.LocalDateTime

class CommentApiTestV2 {
    val restClient = RestClient.create("http://localhost:9001")

    @Test
    fun create() {
        val response1 = create(CommentCreateRequestV2(1L, "comment1", null, 1L))
        println("response1.path = ${response1?.path}")
        println("response1.commentId = ${response1?.commentId}")

        val response2 = create(CommentCreateRequestV2(1L, "comment2", response1?.path, 1L))
        println("\tresponse2.path = ${response2?.path}")
        println("\tresponse2.commentId = ${response2?.commentId}")

        val response3 = create(CommentCreateRequestV2(1L, "comment3", response2?.path,1L))
        println("\t\tresponse3.path = ${response3?.path}")
        println("\t\tresponse3.commentId = ${response3?.commentId}")

        /**
         * response1.path = 00000
         * response1.commentId = 179976767819329536
         * 	response2.path = 0000000000
         * 	response2.commentId = 179976768561721344
         * 		response3.path = 000000000000000
         * 		response3.commentId = 179976768633024512
         */
    }

    fun create(request: CommentCreateRequestV2): CommentResponse? {
        return restClient.post()
            .uri("/v2/comments/create")
            .body(request)
            .retrieve()
            .body(CommentResponse::class.java)
    }

    @Test
    fun read() {
        val response = restClient.get()
            .uri("/v2/comments/179976767819329536")
            .retrieve()
            .body(CommentResponse::class.java)
        println("response = $response")
    }

    @Test
    fun delete() {
        restClient.delete()
            .uri("/v2/comments/delete/179976767819329536")
            .retrieve()
    }

    companion object {
        data class CommentCreateRequestV2(
            val articleId: Long,
            val content: String,
            val parentPath: String?,
            val writerId: Long,
        )

        data class CommentResponse(
            val commentId : Long,
            var content : String,
            var parentCommentId : Long? = null,
            val articleId : Long,
            val writerId : Long,
            var deleted : Boolean,
            val createdAt : LocalDateTime,
            var path: String? = null
        )
    }
}