package kuke.board.hotarticle.repository

import mu.KLogger
import mu.KotlinLogging
import org.springframework.data.redis.connection.StringRedisConnection
import org.springframework.data.redis.core.RedisCallback
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.core.ZSetOperations
import org.springframework.stereotype.Repository
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.streams.toList

@Repository
class HotArticleListRepository(
    private val redisTemplate: StringRedisTemplate,
) {
    private val log = KotlinLogging.logger {}

    // hot-article::list::{yyyyMMdd}
    companion object {
        private val KEY_FORMAT = "hot-article::list::%s"
        private val TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd")
    }

    fun add(articleId: Long, time: LocalDateTime, score: Long, limit: Long, ttl: Duration) {
        // 파이프를 통해 한 번의 연결로 여러번의 연산 가능
        redisTemplate.executePipelined {
            val conn = it as StringRedisConnection
            val key = generateKey(time)
            // sorted set 사용하기 위한 메소드 - z 붙은
            conn.zAdd(key, score.toDouble(), articleId.toString())
            // 상위 10건만 남도록 정리
            // 상위 limit 개수만큼만 유지 가능
            conn.zRemRange(key, 0, -limit - 1)
            conn.expire(key, ttl.toSeconds())
        }
    }

    fun remove(articleId: Long, time: LocalDateTime) {
        redisTemplate.opsForZSet().remove(generateKey(time), articleId.toString())
    }

    fun readAll(dateStr: String): List<Long>? {
        val result = redisTemplate.opsForZSet()
            .reverseRangeWithScores(generateKey(dateStr), 0, -1)
            ?: return null

        return result.mapNotNull {
            val articleId = it.value.toString().toLongOrNull()
            val score = it.score
            log.info { "[HotArticleListRepository.readAll] articleId=$articleId score=$score" }
            articleId
        }
    }

    private fun generateKey(time: LocalDateTime): String
    = generateKey(TIME_FORMATTER.format(time))

    private fun generateKey(dateStr: String): String
    = KEY_FORMAT.format(dateStr)

}