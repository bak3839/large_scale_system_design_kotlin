package kuke.board.hotarticle.repository

import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Repository
import java.time.Duration

@Repository
class ArticleViewCountRepository(
    private val redisTemplate: StringRedisTemplate
) {
    companion object {
        private val KEY_FORMAT = "hot-article::article::%s::view-count"
    }

    fun createOrUpdate(articleId: Long, viewCount: Long, ttl: Duration) {
        redisTemplate.opsForValue().set(generateKey(articleId), viewCount.toString(), ttl)
    }

    fun read(articleId: Long): Long
    = redisTemplate.opsForValue().get(generateKey(articleId))?.toLong() ?: 0L

    private fun generateKey(articleId: Long): String
    = KEY_FORMAT.format(articleId)
}