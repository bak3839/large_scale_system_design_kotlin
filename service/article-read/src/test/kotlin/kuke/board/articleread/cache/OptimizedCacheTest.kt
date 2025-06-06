package kuke.board.articleread.cache

import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.Duration

class OptimizedCacheTest {
    @Test
    fun parseDataTest() {
        parseDataTest("data", 10)
        parseDataTest(3L, 10)
        parseDataTest(3, 10)
        parseDataTest(TestClass("hihi"), 10)
    }

    fun parseDataTest(data: Any, ttlSeconds: Long) {
        // given
        val optimizedCache = OptimizedCache.of(data, Duration.ofSeconds(ttlSeconds))
        println("optimizedCache = ${optimizedCache}")

        // when
        val resolvedData = optimizedCache.parseData(data.javaClass)

        // then
        println("resolvedData = ${resolvedData}")
        assertThat(resolvedData).isEqualTo(data)
    }

    @Test
    fun isExpiredTest() {
        assertThat(OptimizedCache.of("data", Duration.ofDays(-30)).isExpired()).isTrue()
        assertThat(OptimizedCache.of("data", Duration.ofDays(30)).isExpired()).isFalse()
    }

    companion object {
        data class TestClass(
            val testData: String = ""
        )
    }


}