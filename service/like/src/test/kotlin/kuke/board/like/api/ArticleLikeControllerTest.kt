package kuke.board.like.api

import kuke.board.like.service.response.ArticleLikeResponse
import org.junit.jupiter.api.Test
import org.springframework.web.client.RestClient

class LikeApiTest {
    val restClient = RestClient.create("http://localhost:9002")

    @Test
    fun likeAndUnlikeTest() {
        val articleId = 9999L

        like(articleId, 1L)
        like(articleId, 2L)
        like(articleId, 3L)

        val response1 = read(articleId, 1L)
        val response2 = read(articleId, 2L)
        val response3 = read(articleId, 3L)
        println("response1: $response1")
        println("response2: $response2")
        println("response3: $response3")

        unlike(articleId, 1L)
        unlike(articleId, 2L)
        unlike(articleId, 3L)
    }

    private fun like(articleId: Long, userId: Long) {
        restClient.post()
            .uri("/v1/article-likes/articles/$articleId/users/$userId")
            .retrieve()
    }

    private fun unlike(articleId: Long, userId: Long) {
        restClient.delete()
            .uri("/v1/article-likes/articles/$articleId/users/$userId")
            .retrieve()
    }

    private fun read(articleId: Long, userId: Long): ArticleLikeResponse? {
        return restClient.get()
            .uri("/v1/article-likes/articles/$articleId/users/$userId")
            .retrieve()
            .body(ArticleLikeResponse::class.java)
    }
}