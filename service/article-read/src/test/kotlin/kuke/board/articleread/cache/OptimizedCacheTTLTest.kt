package kuke.board.articleread.cache

import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.Duration

class OptimizedCacheTTLTest {
    @Test
    fun ofTest() {
        // given
        val ttlSeconds = 10L

        // when
        val optimizedCacheTTL = OptimizedCacheTTL.of(ttlSeconds)

        // then
        assertThat(optimizedCacheTTL.logicalTTL).isEqualTo(Duration.ofSeconds(ttlSeconds))
        assertThat(optimizedCacheTTL.physicalTTL).isEqualTo(
            Duration.ofSeconds(ttlSeconds).plusSeconds(OptimizedCacheTTL.PHYSICAL_TTL_DELAY_SECONDS)
        )
    }
}