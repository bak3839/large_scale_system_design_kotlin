package kuke.board.articleread.cache

import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class OptimizedCacheLockProvider(
    private val redisTemplate: StringRedisTemplate
) {
    companion object {
        private val KEY_PREFIX = "optimized-cache-lock::"
        private val LOCK_TTL = Duration.ofSeconds(3)
    }

    fun lock(key: String): Boolean {
        return redisTemplate.opsForValue().setIfAbsent(
            generateKey(key),
            "",
            LOCK_TTL
        ) ?: throw RuntimeException("Unable to acquire lock for $key")
    }

    fun unlock(key: String) {
        redisTemplate.delete(generateKey(key))
    }

    fun generateKey(key: String): String
    = KEY_PREFIX + key
}