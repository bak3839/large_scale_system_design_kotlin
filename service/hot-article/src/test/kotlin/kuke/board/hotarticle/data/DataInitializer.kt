package kuke.board.hotarticle.data

import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.view
import org.springframework.web.client.RestClient
import java.time.LocalDateTime
import java.util.random.RandomGenerator

class DataInitializer {
    val articleServiceClient = RestClient.create("http://localhost:9000")
    val commentServiceClient = RestClient.create("http://localhost:9001")
    val likeServiceClient = RestClient.create("http://localhost:9002")
    val viewServiceClient = RestClient.create("http://localhost:9003")

    @Test
    fun initialize() {
        for(i in 0 until 1) {
            val articleId = createArticle()
            val commentCount = RandomGenerator.getDefault().nextLong(3L)
            val likeCount = RandomGenerator.getDefault().nextLong(5L)
            val viewCount = RandomGenerator.getDefault().nextLong(100L)

            createComment(articleId, commentCount)
            like(articleId, likeCount)
            view(articleId, viewCount)
        }
    }

    private fun createArticle(): Long {
        return articleServiceClient.post()
            .uri("/v1/articles/create")
            .body(ArticleCreateRequest("title", "content", 10L, 10L))
            .retrieve()
            .body(ArticleResponse::class.java)!!
            .articleId
    }

    private fun createComment(articleId: Long, commentCount: Long) {
        var count = commentCount
        while(count-- > 0) {
            commentServiceClient.post()
                .uri("/v2/comments/create")
                .body(CommentCreateRequestV2(
                    articleId = articleId,
                    content = "content",
                    parentPath = null,
                    writerId = 1L
                ))
                .retrieve()
                .body(CommentResponse::class.java)
        }
    }

    private fun like(articleId: Long, likeCount: Long) {
        var count = likeCount
        while(count-- > 0) {
            likeServiceClient.post()
                .uri("/v1/article-likes/articles/$articleId/users/${count}/pessimistic-lock-1")
                .retrieve()
        }
    }

    private fun view(articleId: Long, viewCount: Long) {
        var count = viewCount
        while(count-- > 0) {
            viewServiceClient.post()
                .uri("/v1/article-views/articles/$articleId/users/$count")
                .retrieve()
        }
    }

    companion object {
        data class ArticleCreateRequest(
            val title: String,
            val content: String,
            val boardId: Long,
            val writerId: Long
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

        data class CommentCreateRequestV2(
            val articleId: Long,
            val content: String,
            val parentPath: String?,
            val writerId: Long,
        )

        data class CommentResponse(
            val commentId : Long,
            var content : String,
            var parentCommentId : Long? = null,
            val articleId : Long,
            val writerId : Long,
            var deleted : Boolean,
            val createdAt : LocalDateTime,
            var path: String? = null
        )
    }
}