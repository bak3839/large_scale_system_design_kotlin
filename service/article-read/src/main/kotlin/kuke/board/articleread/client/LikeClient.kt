package kuke.board.articleread.client

import jakarta.annotation.PostConstruct
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

@Component
class LikeClient {
    private lateinit var restClient: RestClient

    @Value("\${endpoints.board-like-service.url}")
    private lateinit var likeServiceUrl: String

    private val log = KotlinLogging.logger {}

    @PostConstruct
    fun initRestClient() {
        restClient = RestClient.create(likeServiceUrl)
    }

    fun count(articleId: Long): Long {
        try {
            return restClient.get()
                .uri("/v1/article-likes/articles/$articleId/count")
                .retrieve()
                .body(Long::class.java) ?: 0
        } catch (e: Exception) {
            log.error(e) {"[LikeClient.count] articleId=$articleId"}
            return 0
        }
    }
}