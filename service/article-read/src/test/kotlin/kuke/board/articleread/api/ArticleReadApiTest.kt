package kuke.board.articleread.api

import kuke.board.articleread.service.response.ArticleReadPageResponse
import kuke.board.articleread.service.response.ArticleReadResponse
import org.junit.jupiter.api.Test
import org.springframework.core.ParameterizedTypeReference
import org.springframework.web.client.RestClient

class ArticleReadApiTest {
    val articleReadRestClient: RestClient = RestClient.create("http://localhost:9005")
    val articleRestClient: RestClient = RestClient.create("http://localhost:9000")

    @Test
    fun readTest() {
        val response = articleReadRestClient.get()
            .uri("/v1/articles/${177342987480424448}")
            .retrieve()
            .body(ArticleReadResponse::class.java)

        println("response = ${response}")
    }

    @Test
    fun readAllTest() {
        val response1 = articleReadRestClient.get()
            .uri("/v1/articles?boardId=1&page=1&pageSize=5")
            .retrieve()
            .body(ArticleReadPageResponse::class.java)

        println("response1.getArticleCount() = ${response1?.articleCount}")

        response1?.articles?.forEach { println("article.getArticleId() = ${it.articleId}") }

        val response2 = articleRestClient.get()
            .uri("/v1/articles/all?boardId=1&page=1&pageSize=5")
            .retrieve()
            .body(ArticleReadPageResponse::class.java)

        println("response1.getArticleCount() = ${response2?.articleCount}")

        response2?.articles?.forEach { println("article.getArticleId() = ${it.articleId}") }
    }

    @Test
    fun readAllInfiniteScrollTest() {
        val response1 = articleReadRestClient.get()
            .uri("/v1/articles/infinite-scroll?boardId=1&pageSize=5")
            .retrieve()
            .body(object : ParameterizedTypeReference<List<ArticleReadResponse>>() {})

        response1?.forEach { println("response = ${it.articleId}") }

        val response2 = articleRestClient.get()
            .uri("/v1/articles/infinite-scroll?boardId=1&lastArticleId=&pageSize=5")
            .retrieve()
            .body(object : ParameterizedTypeReference<List<ArticleReadResponse>>() {})

        response2?.forEach { println("response = ${it.articleId}") }
    }
}