package kuke.board.hotarticle.repository

import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Repository
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

/**
 * 좋아요 이벤트가 왔는데, 이 이벤트에 대한 게시글이 오늘 게시글인지 확인하려면 게시글 서비스 조회가 필요
 * 하지만 게시글 생성 시간을 저장하고 있으면, 오늘 게시글인지 게시글 서비스 조회 없이도 알 수 있음
 */
@Repository
class ArticleCreatedTimeRepository(
    private val redisTemplate: StringRedisTemplate
) {
    companion object {
        private val KEY_FORMAT = "hot-article::article::%s::created-time"
    }

    fun createOrUpdate(articleId: Long, createdAt: LocalDateTime, ttl: Duration) {
        redisTemplate.opsForValue().set(
            generateKey(articleId),
            createdAt.toInstant(ZoneOffset.UTC).toEpochMilli().toString(),
            ttl
        )
    }

    fun delete(articleId: Long) {
        redisTemplate.delete(generateKey(articleId))
    }

    fun read(articleId: Long): LocalDateTime? {
        val result =  redisTemplate.opsForValue().get(generateKey(articleId)) ?: return null
        return LocalDateTime.ofInstant(
            Instant.ofEpochMilli(result.toLong()), ZoneOffset.UTC
        )
    }

    private fun generateKey(articleId: Long): String
    = KEY_FORMAT.format(articleId)
}