package kuke.board.articleread.learning

import org.junit.jupiter.api.Test

class LongToDoubleTest {
    @Test
    fun longToDoubleTEst() {
        val longValue = 111_111_111_111_111_111L;
        println("longValue = ${longValue}")

        val doubleValue: Double = longValue.toDouble()
        println("doubleValue = ${doubleValue}")

        val longValue2 = doubleValue.toLong()
        println("longValue2 = ${longValue2}")
    }
}