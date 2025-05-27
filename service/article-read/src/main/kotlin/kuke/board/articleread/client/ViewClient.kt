package kuke.board.articleread.client

import jakarta.annotation.PostConstruct
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

@Component
class ViewClient {
    private lateinit var restClient: RestClient

    @Value("\${endpoints.board-view-service.url}")
    private lateinit var viewServiceUrl: String

    private val log = KotlinLogging.logger {}

    @PostConstruct
    fun initRestClient() {
        restClient = RestClient.create(viewServiceUrl)
    }

    fun count(articleId: Long): Long {
        try {
            return restClient.get()
                .uri("/v1/article-views/articles/$articleId/count")
                .retrieve()
                .body(Long::class.java) ?: 0
        } catch (e: Exception) {
            log.error(e) {"[ViewClient.count] articleId=$articleId"}
            return 0
        }
    }
}