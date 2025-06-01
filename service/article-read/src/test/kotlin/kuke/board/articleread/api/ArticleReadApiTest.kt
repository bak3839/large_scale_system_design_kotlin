package kuke.board.articleread.api

import kuke.board.articleread.service.response.ArticleReadResponse
import org.junit.jupiter.api.Test
import org.springframework.web.client.RestClient

class ArticleReadApiTest {
    val restClient: RestClient = RestClient.create("http://localhost:9005")

    @Test
    fun readTest() {
        val response = restClient.get()
            .uri("/v1/articles/${177342987480424448}")
            .retrieve()
            .body(ArticleReadResponse::class.java)

        println("response = ${response}")
    }
}