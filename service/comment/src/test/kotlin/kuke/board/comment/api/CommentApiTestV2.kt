package kuke.board.comment.api

import kuke.board.comment.service.request.CommentCreateRequestV2
import kuke.board.comment.service.response.CommentPageResponse
import kuke.board.comment.service.response.CommentResponse
import org.junit.jupiter.api.Test
import org.springframework.core.ParameterizedTypeReference
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

    @Test
    fun readAll() {
        val response = restClient.get()
            .uri("/v2/comments/readAll?articleId=1&page=50000&pageSize=10")
            .retrieve()
            .body(CommentPageResponse::class.java)

        println("response.commentCount = ${response?.commentCount}")
        response?.comments?.forEach { println("comment.commentId = ${it.commentId}") }
        /**
         * response.commentCount = 101
         * comment.commentId = 179980695442100230
         * comment.commentId = 179980695542763526
         * comment.commentId = 179980695542763534
         * comment.commentId = 179980695542763542
         * comment.commentId = 179980695542763549
         * comment.commentId = 179980695542763559
         * comment.commentId = 179980695542763565
         * comment.commentId = 179980695546957827
         * comment.commentId = 179980695546957835
         * comment.commentId = 179980695546957845
         *
         * response.commentCount = 500001
         * comment.commentId = 179980857400955334
         * comment.commentId = 179980857400955335
         * comment.commentId = 179980857400955336
         * comment.commentId = 179980857400955337
         * comment.commentId = 179980857400955338
         * comment.commentId = 179980857400955339
         * comment.commentId = 179980857400955340
         * comment.commentId = 179980857400955341
         * comment.commentId = 179980857400955342
         * comment.commentId = 179980857400955343
         */
    }

    @Test
    fun readAllInfiniteScroll() {
        val response1 = restClient.get()
            .uri("/v2/comments/readAll/infinite-scroll?articleId=1&pageSize=5")
            .retrieve()
            .body(object : ParameterizedTypeReference<List<CommentResponse>>(){
            })

        println("firstPage")
        response1?.forEach { println("response1.commentId = ${it.commentId}") }

        val lastPath = response1?.last()?.path

        val response2 = restClient.get()
            .uri("/v2/comments/readAll/infinite-scroll?articleId=1&pageSize=5&lastPath=${lastPath}")
            .retrieve()
            .body(object : ParameterizedTypeReference<List<CommentResponse>>(){
            })

        println("secondPage")
        response2?.forEach { println("response2.commentId = ${it.commentId}") }
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