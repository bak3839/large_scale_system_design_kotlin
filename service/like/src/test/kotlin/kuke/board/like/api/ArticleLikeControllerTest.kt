package kuke.board.like.api

import kuke.board.like.service.response.ArticleLikeResponse
import org.junit.jupiter.api.Test
import org.springframework.web.client.RestClient
import java.util.concurrent.CountDownLatch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class LikeApiTest {
    val restClient = RestClient.create("http://localhost:9002")

//    @Test
//    fun likeAndUnlikeTest() {
//        val articleId = 9999L
//
//        like(articleId, 1L)
//        like(articleId, 2L)
//        like(articleId, 3L)
//
//        val response1 = read(articleId, 1L)
//        val response2 = read(articleId, 2L)
//        val response3 = read(articleId, 3L)
//        println("response1: $response1")
//        println("response2: $response2")
//        println("response3: $response3")
//
//        unlike(articleId, 1L)
//        unlike(articleId, 2L)
//        unlike(articleId, 3L)
//    }

    private fun like(articleId: Long, userId: Long, lockType: String) {
        restClient.post()
            .uri("/v1/article-likes/articles/$articleId/users/$userId/$lockType")
            .retrieve()
    }

    private fun unlike(articleId: Long, userId: Long, lockType: String) {
        restClient.delete()
            .uri("/v1/article-likes/articles/$articleId/users/$userId$lockType")
            .retrieve()
    }

    private fun read(articleId: Long, userId: Long): ArticleLikeResponse? {
        return restClient.get()
            .uri("/v1/article-likes/articles/$articleId/users/$userId")
            .retrieve()
            .body(ArticleLikeResponse::class.java)
    }

    @Test
    fun likePerformanceTest() {
        val executors = Executors.newFixedThreadPool(100)

        likePerformanceTest(executors, 11111L, "pessimistic-lock1")
        likePerformanceTest(executors, 22222L, "pessimistic-lock2")
        likePerformanceTest(executors, 33333L, "optimistic-lock")
    }

    private fun likePerformanceTest(executors: ExecutorService, articleId: Long, lockType: String) {
        val latch = CountDownLatch(3000)
        println("$lockType start")

        like(articleId, 1L, lockType)

        val start = System.nanoTime()

        for (i in 1..3000) {
            val userId: Long = i.toLong() + 2L

            executors.submit {
                like(articleId, userId, lockType)
                latch.countDown()
            }
        }
        latch.await()

        val end = System.nanoTime()

        println("lockType = $lockType, time = ${(end - start) / 1000000} ms")
        println("$lockType end")

        val count: Long? = restClient.get()
            .uri("/v1/article-likes/articles/$articleId/count")
            .retrieve()
            .body(Long::class.java)

        println("count = $count")
    }
}