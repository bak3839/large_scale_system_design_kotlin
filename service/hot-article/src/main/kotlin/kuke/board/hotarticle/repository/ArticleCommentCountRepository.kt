package kuke.board.hotarticle.repository

import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Repository
import java.time.format.DateTimeFormatter
import java.time.Duration

@Repository
class ArticleCommentCountRepository(
    private val redisTemplate: StringRedisTemplate,
) {
    companion object {
        private val KEY_FORMAT = "hot-article::article::%s::comment-count"
    }

    fun createOrUpdate(articleId: Long, commentCount: Long, ttl: Duration) {
        redisTemplate.opsForValue().set(generateKey(articleId), commentCount.toString(), ttl)
    }

    fun read(articleId: Long): Long
    = redisTemplate.opsForValue().get(generateKey(articleId))?.toLong() ?: 0L

    private fun generateKey(articleId: Long): String
    = KEY_FORMAT.format(articleId)
}