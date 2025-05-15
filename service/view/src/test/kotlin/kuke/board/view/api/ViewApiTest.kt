package kuke.board.view.api

import org.junit.jupiter.api.Test
import org.springframework.web.client.RestClient
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors


class ViewApiTest {
    val restClient = RestClient.create("http://localhost:9003")

    @Test
    fun viewTest() {
        val executors = Executors.newFixedThreadPool(100)
        val latch = CountDownLatch(10000)

        for(i in 1..10000) {
            executors.submit {
                restClient.post()
                    .uri("/v1/article-views/articles/5/users/1")
                    .retrieve()

                latch.countDown()
            }
        }

        latch.await()

        val count = restClient.get()
            .uri("/v1/article-views/articles/5/count")
            .retrieve()
            .body(Long::class.java)

        println("count: $count")
        executors.shutdown()
    }
}