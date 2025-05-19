package kuke.board.hotarticle.utils

import org.junit.jupiter.api.Test

class TimeCalculatorUtilsTest {
    @Test
    fun test() {
        val duration = TimeCalculatorUtils.calculateDurationToMidnight()
        println("duration.seconds / 60 = ${duration.seconds / 60}")
    }
}