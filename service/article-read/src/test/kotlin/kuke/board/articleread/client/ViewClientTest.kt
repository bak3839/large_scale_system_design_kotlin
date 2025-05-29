package kuke.board.articleread.client

import org.junit.jupiter.api.Assertions.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.util.concurrent.TimeUnit
import kotlin.test.Test

@SpringBootTest
class ViewClientTest {
    @Autowired
    lateinit var viewClient: ViewClient

    @Test
    fun readCacheableTest() {
        viewClient.count(1L)
        viewClient.count(1L)
        viewClient.count(1L)

        TimeUnit.SECONDS.sleep(3)
        viewClient.count(1L)
    }
}