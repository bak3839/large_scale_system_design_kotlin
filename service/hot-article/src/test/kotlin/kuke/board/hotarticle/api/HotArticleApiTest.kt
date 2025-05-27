package kuke.board.hotarticle.api

import kuke.board.hotarticle.service.response.HotArticleResponse
import org.junit.jupiter.api.Test
import org.springframework.core.ParameterizedTypeReference
import org.springframework.web.client.RestClient

class HotArticleApiTest {
    val restClient = RestClient.create("http://localhost:9004")

    @Test
    fun readAllTest() {
        val response = restClient.get()
            .uri("/v1/hot-article/articles/date/20250524}")
            .retrieve()
            .body(object : ParameterizedTypeReference<List<HotArticleResponse>>() {})

        response?.forEach { println(it) }
    }
}