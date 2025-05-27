package kuke.board.articleread.client

import jakarta.annotation.PostConstruct
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import java.time.LocalDateTime

@Component
class ArticleClient {
    private lateinit var restClient: RestClient

    @Value("\${endpoints.board-article-service.url}")
    private lateinit var articleServiceUrl: String

    private val log = KotlinLogging.logger {}

    @PostConstruct
    fun initRestClient() {
        restClient = RestClient.create(articleServiceUrl)
    }

    fun read(articleId: Long): ArticleResponse? {
        try {
            val articleResponse = restClient.get()
                .uri("/v1/articles/$articleId")
                .retrieve()
                .body(ArticleResponse::class.java)
            return articleResponse
        } catch (e: Exception) {
            log.error(e) {"[ArticleClient.read] articleId=$articleId"}
            return null
        }
    }

    companion object {
        data class ArticleResponse(
            var articleId: Long,
            var title: String,
            var content: String,
            var boardId: Long, // shard key
            var writerId: Long,
            var createdAt: LocalDateTime = LocalDateTime.now(),
            var modifiedAt: LocalDateTime = LocalDateTime.now(),
        )
    }
}