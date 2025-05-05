package article.service

import kuke.board.article.service.PageCalculator
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class PageCalculatorTest {
    @Test
    fun calculatePageLimitTest() {
        calculatePateLimitTest(1L, 30L, 10L, 301L)
        calculatePateLimitTest(7L, 30L, 10L, 301L)
        calculatePateLimitTest(10L, 30L, 10L, 301L)
        calculatePateLimitTest(11L, 30L, 10L, 601L)
        calculatePateLimitTest(12L, 30L, 10L, 601L)
    }

    fun calculatePateLimitTest(page: Long, pageSize: Long, movablePageCount: Long, executed: Long) {
        val result = PageCalculator.calculatePageLimit(page, pageSize, movablePageCount)
        assertEquals(executed, result)
    }
}