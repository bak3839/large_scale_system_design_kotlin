package kuke.board.articleread.client

import jakarta.annotation.PostConstruct
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.ParameterizedTypeReference
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

    fun readAll(boardId: Long, page: Long, pageSize: Long): ArticlePageResponse? {
        try {
            return restClient.get()
                .uri("/v1/articles/all?boardId=$boardId&page=$page&pageSize=$pageSize")
                .retrieve()
                .body(ArticlePageResponse::class.java)
        } catch (e: Exception) {
            log.error(e) { "[ArticleClient.readAll] boardId=$boardId, page=$page, pageSize=$pageSize" }
            return ArticlePageResponse.EMPTY
        }
    }

    fun readAllInfiniteScroll(boardId: Long, lastArticleId: Long?, pageSize: Long): List<ArticleResponse>? {
        try {
            return restClient.get()
                .uri(
                    lastArticleId?.let {
                        "/v1/articles/infinite-scroll?boardId=$boardId&lastArticleId=$lastArticleId&pageSize=$pageSize"
                    } ?: "/v1/articles/infinite-scroll?boardId=$boardId&pageSize=$pageSize"
                )
                .retrieve()
                .body(object : ParameterizedTypeReference<List<ArticleResponse>>() {})
        } catch (e: Exception) {
            log.error(e) {"[ArticleClient.readAllInfiniteScroll] boardId=$boardId lastArticleId=$lastArticleId pageSize=$pageSize" }
            return emptyList()
        }
    }

    fun count(boardId: Long): Long {
        try {
            return restClient.get()
                .uri("/v1/articles/boards/$boardId/count")
                .retrieve()
                .body(Long::class.java) ?: 0L
        } catch (e: Exception) {
            log.error(e) {"[ArticleClient.count] boardId=$boardId"}
            return 0L
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

        data class ArticlePageResponse(
            val articles: List<ArticleResponse>,
            val articleCount: Long
        ) {
            companion object {
                val EMPTY = ArticlePageResponse(listOf(), 0L)
            }
        }
    }
}