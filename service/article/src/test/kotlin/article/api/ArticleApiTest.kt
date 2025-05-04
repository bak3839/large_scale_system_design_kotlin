package article.api

import org.junit.jupiter.api.Test
import org.springframework.web.client.RestClient
import java.time.LocalDateTime

class ArticleApiTest {
    val restClient: RestClient = RestClient.create("http://localhost:9000")

    @Test
    fun createTest() {
        val response = create(
            ArticleCreateRequest(
                title = "Hi",
                content = "Hi there!",
                writerId = 1L,
                boardId = 1L
            )
        )
        println(response)
    }

    private fun create(request: ArticleCreateRequest): ArticleResponse? {
        return restClient.post()
            .uri("/v1/articles/create")
            .body(request)
            .retrieve()
            .body(ArticleResponse::class.java)
    }

    @Test
    fun readTest() {
        val response = read(177332402605629440L)
        println(response)
    }

    private fun read(articleId: Long) : ArticleResponse? {
        return restClient.get()
            .uri("/v1/articles/$articleId")
            .retrieve()
            .body(ArticleResponse::class.java)
    }

    @Test
    fun updateTest() {
        val response = update(177332402605629440L)
        println(response)
    }

    fun update(articleId: Long): ArticleResponse?  {
        return restClient.put()
            .uri("v1/articles/$articleId/update")
            .body(ArticleUpdateRequest("Hi Update", "content updating"))
            .retrieve()
            .body(ArticleResponse::class.java)
    }

    @Test
    fun deleteTest() {
        restClient.delete()
            .uri("/v1/articles/177332402605629440/delete")
            .retrieve()
    }

    companion object {
        data class ArticleCreateRequest(
            val title: String,
            val content: String,
            val boardId: Long,
            val writerId: Long
        )

        data class ArticleUpdateRequest(
            val title: String,
            val content: String,
        )

        data class ArticleResponse(
            var articleId: Long,
            var title: String,
            var content: String,
            var boardId: Long, // shard key
            var writerId: Long,
            var createdAt: LocalDateTime = LocalDateTime.now(),
            var updatedAt: LocalDateTime = LocalDateTime.now(),
        )
    }
}
