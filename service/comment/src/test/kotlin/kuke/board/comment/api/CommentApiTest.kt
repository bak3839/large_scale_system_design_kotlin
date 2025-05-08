package kuke.board.comment.api

import kuke.board.comment.service.response.CommentPageResponse
import kuke.board.comment.service.response.CommentResponse
import org.junit.jupiter.api.Test
import org.springframework.core.ParameterizedTypeReference
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

    @Test
    fun readAll() {
        val response = restClient.get()
            .uri("v1/comments/readAll?articleId=1&pageSize=10&page=2")
            .retrieve()
            .body(CommentPageResponse::class.java)

        println("response.getCommentCount: ${response?.commentCount}")

        response?.comments?.forEach {
            if(it.commentId != it.parentCommentId) print("\t")
            println("comment.getCommentId: ${it.commentId}")
        }
        /**
         * 1번 페이지 수행 결과
         * response.getCommentCount: 101
         * comment.getCommentId: 178507571313090560
         * 	comment.getCommentId: 178507571413753860
         * comment.getCommentId: 178507571313090561
         * 	comment.getCommentId: 178507571413753863
         * comment.getCommentId: 178507571313090562
         * 	comment.getCommentId: 178507571413753859
         * comment.getCommentId: 178507571313090563
         * 	comment.getCommentId: 178507571413753857
         * comment.getCommentId: 178507571313090564
         * 	comment.getCommentId: 178507571413753862
         *
         * 2번 페이지 수행 결과
         * comment.getCommentId: 178507571313090565
         * 	comment.getCommentId: 178507571413753861
         * comment.getCommentId: 178507571313090566
         * 	comment.getCommentId: 178507571413753870
         * comment.getCommentId: 178507571313090567
         * 	comment.getCommentId: 178507571413753856
         * comment.getCommentId: 178507571313090568
         * 	comment.getCommentId: 178507571413753864
         * comment.getCommentId: 178507571313090569
         * 	comment.getCommentId: 178507571413753858
         */
    }

    @Test
    fun readAllInfiniteScroll() {
        println("firstPage")
        val response1 = restClient.get()
            .uri("/v1/comments/readAll/infinite-scroll?articleId=1&pageSize=10")
            .retrieve()
            .body(object : ParameterizedTypeReference<List<CommentResponse>>(){})

        response1?.forEach {
            if(it.commentId != it.parentCommentId) print("\t")
            println("comment.getCommentId: ${it.commentId}")
        }

        val lastParentCommentId = response1?.last()?.parentCommentId
        val lastCommentId = response1?.last()?.commentId

        println("secondPage")
        val response2 = restClient.get()
            .uri("/v1/comments/readAll/infinite-scroll?articleId=1&pageSize=10&lastParentCommentId=$lastParentCommentId&lastCommentId=$lastCommentId")
            .retrieve()
            .body(object : ParameterizedTypeReference<List<CommentResponse>>(){})

        response2?.forEach {
            if(it.commentId != it.parentCommentId) print("\t")
            println("comment.getCommentId: ${it.commentId}")
        }
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

