package kuke.board.articleread.client

import jakarta.annotation.PostConstruct
import kuke.board.articleread.cache.OptimizedCacheable
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.annotation.Cacheable
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

    //@Cacheable(key = "#articleId", value = ["articleViewCount"])
    @OptimizedCacheable(type = "articleViewCount", ttlSeconds = 1)
    fun count(articleId: Long): Long {
        log.info { "[ViewClient.count] $articleId" }
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