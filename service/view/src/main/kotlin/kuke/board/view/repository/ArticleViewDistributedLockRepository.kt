package kuke.board.view.repository

import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Repository
import java.time.Duration

@Repository
class ArticleViewDistributedLockRepository(
    private val redisTemplate: StringRedisTemplate
) {
    // view::article::{article_id}::user::{user_id}::lock
    private val KEY_FORMAT = "view::article::%s::user::%s::lock"

    fun lock(articleId: Long, userId: Long, ttl: Duration): Boolean? {
        val key = generateKey(articleId, userId, ttl)
        return redisTemplate.opsForValue().setIfAbsent(key, "", ttl)
    }

    private fun generateKey(articleId: Long, userId: Long, ttl: Duration): String
    = KEY_FORMAT.format(articleId, userId)
}