package kuke.board.articleread.client

import jakarta.annotation.PostConstruct
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import java.time.LocalDateTime

@Component
class CommentClient {
    private lateinit var restClient: RestClient

    @Value("\${endpoints.board-comment-service.url}")
    private lateinit var commentServiceUrl: String

    private val log = KotlinLogging.logger {}

    @PostConstruct
    fun initRestClient() {
        restClient = RestClient.create(commentServiceUrl)
    }

    fun count(articleId: Long): Long {
        try {
            return restClient.get()
                .uri("/v2/comments/articles/$articleId/count")
                .retrieve()
                .body(Long::class.java) ?: 0
        } catch (e: Exception) {
            log.error(e) {"[CommentClient.count] articleId=$articleId"}
            return 0
        }
    }
}