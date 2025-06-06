package kuke.board.articleread.cache

import java.time.Duration

class OptimizedCacheTTL(
    val logicalTTL: Duration,
    val physicalTTL: Duration
) {
    companion object {
        const val PHYSICAL_TTL_DELAY_SECONDS = 5L

        fun of(ttlSeconds: Long): OptimizedCacheTTL
        = OptimizedCacheTTL(
            logicalTTL = Duration.ofSeconds(ttlSeconds),
            physicalTTL = Duration.ofSeconds(ttlSeconds).plusSeconds(PHYSICAL_TTL_DELAY_SECONDS)
        )
    }
}