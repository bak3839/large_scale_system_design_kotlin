package kuke.board.view.repository

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Repository

@Repository
class ArticleViewCountRepository(
    private val redisTemplate: StringRedisTemplate
) {

    companion object {}
    // view::article::{articleId}::view_count
    private val KEY_FORMAT = "view::article::%s::view_count"

    fun read(articleId: Long): Long {
        val result = redisTemplate.opsForValue().get(generateKey(articleId))
        return result?.toLong() ?: 0L
    }

    fun increase(articleId: Long) : Long?
    = redisTemplate.opsForValue().increment(generateKey(articleId))

    private fun generateKey(articleId: Long): String
    = KEY_FORMAT.format(articleId)
}