package kuke.board.common.snowflake

import java.util.random.RandomGenerator

class Snowflake {
    companion object {
        private val UNUSED_BITS: Int = 1
        private val EPOCH_BITS: Int = 41
        private val NODE_ID_BITS: Int = 10
        private val SEQUENCE_BITS: Int = 12

        private val maxNodeId: Long = (1L shl NODE_ID_BITS) - 1
        private val maxSequence: Long = (1L shl SEQUENCE_BITS) - 1
    }

    private val nodeId: Long = RandomGenerator.getDefault().nextLong(maxNodeId + 1)
    // UTC = 2024-01-01T00:00:00Z
    private val startTimeMillis: Long = 1704067200000L

    private var lastTimeMillis = startTimeMillis
    private var sequence = 0L

    @Synchronized
    fun nextId(): Long {
        var currentTimeMillis = System.currentTimeMillis()

        when {
            currentTimeMillis < lastTimeMillis -> throw IllegalStateException("Invalid Time")
            currentTimeMillis == lastTimeMillis -> {
                sequence = (sequence + 1) and maxSequence
                if(sequence == 0L) {
                    currentTimeMillis = waitNextMillis(currentTimeMillis)
                }
            }
            else -> sequence = 0L
        }

        lastTimeMillis = currentTimeMillis

        return ((currentTimeMillis - startTimeMillis) shl (NODE_ID_BITS + SEQUENCE_BITS)) or (nodeId shl SEQUENCE_BITS) or sequence
    }

    private fun waitNextMillis(currentTimestamp: Long): Long {
        var timestamp = currentTimestamp
        while(timestamp <= lastTimeMillis) {
            timestamp = System.currentTimeMillis()
        }
        return timestamp
    }
}