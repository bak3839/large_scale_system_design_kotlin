package kuke.board.hotarticle.repository

import io.kotest.matchers.shouldBe
import kuke.board.hotarticle.HotArticleApplication
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

@SpringBootTest(classes = [HotArticleApplication::class])
class HotArticleListRepositoryTest {
    @Autowired
    lateinit var hotArticleListRepository: HotArticleListRepository

    @Test
    fun addTest() {
        // given
        val time = LocalDateTime.of(2025, 5, 19, 0, 0)
        val limit = 3L

        // when
        hotArticleListRepository.add(1L, time, 2L, limit, Duration.ofSeconds(3))
        hotArticleListRepository.add(2L, time, 3L, limit, Duration.ofSeconds(3))
        hotArticleListRepository.add(3L, time, 1L, limit, Duration.ofSeconds(3))
        hotArticleListRepository.add(4L, time, 5L, limit, Duration.ofSeconds(3))
        hotArticleListRepository.add(5L, time, 4L, limit, Duration.ofSeconds(3))

        // then
        val articleIds = hotArticleListRepository.readAll("20250519")
        assertNotNull(articleIds)
        articleIds?.let {
            articleIds.size shouldBe limit.toInt()
            articleIds.get(0) shouldBe 4L
            articleIds.get(1) shouldBe 5L
            articleIds.get(2) shouldBe 2L
        }

        TimeUnit.SECONDS.sleep(5)

        assertEquals(true, hotArticleListRepository.readAll("20250519")?.isEmpty())
    }
}