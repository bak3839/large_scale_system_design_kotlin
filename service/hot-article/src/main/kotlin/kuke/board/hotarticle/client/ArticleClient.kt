package kuke.board.hotarticle.client

import jakarta.annotation.PostConstruct
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import java.time.LocalDateTime

@Component
class ArticleClient {
    private lateinit var restClient: RestClient
    private val log = KotlinLogging.logger {}

    @Value("\${endpoints.board-article-service.url}")
    private lateinit var articleServerUrl: String

    @PostConstruct
    fun initRestClient() {
        restClient = RestClient.create(articleServerUrl)
    }

    fun read(articleId: Long) : ArticleResponse? {
        return try {
            restClient.get()
                .uri("/v1/articles/$articleId")
                .retrieve()
                .body(ArticleResponse::class.java)
        } catch (e: Exception) {
            log.error(e) { "[ArticleClient.read] articleId=$articleId" }
            null
        }
    }

    companion object {
        data class ArticleResponse(
            val articleId: Long,
            val title: String,
            val createdAt: LocalDateTime
        )
    }
}