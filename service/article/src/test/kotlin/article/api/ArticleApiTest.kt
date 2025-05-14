package article.api

import kuke.board.article.service.response.ArticlePageResponse
import org.junit.jupiter.api.Test
import org.springframework.core.ParameterizedTypeReference
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

    @Test
    fun readAllTest() {
        val response = restClient.get()
            .uri("/v1/articles/all?boardId=1&page=50000&pageSize=30")
            .retrieve()
            .body(ArticlePageResponse::class.java)

        println("response.getArticleCount() = ${response?.articleCount}")

        for(article in response!!.articles) {
            println("article = $article")
        }
    }

    @Test
    fun readAllInfiniteScrollTest() {
        val articles = restClient.get()
            .uri("/v1/articles/infinite-scroll?boardId=1&pageSize=30")
            .retrieve()
            .body(object : ParameterizedTypeReference<List<ArticleResponse>>() {})


        println("firstPage")
        articles?.forEach {
            println("articleResponse.getArticleId() = ${it.articleId}")
        }

        val lastArticleId = articles?.last()?.articleId
        val articles2 = restClient.get()
            .uri("/v1/articles/infinite-scroll?boardId=1&pageSize=30&lastArticleId=$lastArticleId")
            .retrieve()
            .body(object : ParameterizedTypeReference<List<ArticleResponse>>() {})

        articles2?.forEach {
            println("articleResponse.getArticleId() = ${it.articleId}")
        }
    }

    @Test
    fun countTest() {
        val response = create(
            ArticleCreateRequest(
                title = "Hi",
                content = "content!",
                writerId = 1L,
                boardId = 2L
            )
        )

        val count1 = restClient.get()
            .uri("/v1/articles/boards/2/count")
            .retrieve()
            .body(Long::class.java)

        println("count1 = $count1")

        restClient.delete()
            .uri("/v1/articles/${response?.articleId}/delete")
            .retrieve()

        val count2 = restClient.get()
            .uri("/v1/articles/boards/2/count")
            .retrieve()
            .body(Long::class.java)

        println("count2 = $count2")
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
