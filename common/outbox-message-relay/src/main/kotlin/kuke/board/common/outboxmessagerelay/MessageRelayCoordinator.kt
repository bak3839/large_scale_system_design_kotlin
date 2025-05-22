package kuke.board.common.outboxmessagerelay

import jakarta.annotation.PreDestroy
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.connection.StringRedisConnection
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.Instant
import java.util.*
import java.util.concurrent.TimeUnit

@Component
class MessageRelayCoordinator(
    private val redisTemplate: StringRedisTemplate
) {
    @Value("\${spring.application.name}")
    lateinit var applicationName: String

    private val APP_ID = UUID.randomUUID().toString()
    private val PING_INTERVAL_SECONDS = 3L
    private val PING_FAILURE_THRESHOLD = 3

    fun assignShards(): AssignedShard
    = AssignedShard.of(
        appId = APP_ID,
        appIds = findAppIds(),
        shardCount = MessageRelayConstants.SHARD_COUNT
    )

    private fun findAppIds(): List<String>
    = redisTemplate.opsForZSet().reverseRange(generateKey(), 0, -1)?.toList()?.sorted()
        ?: emptyList()

    @Scheduled(fixedDelay = 3, timeUnit = TimeUnit.SECONDS)
    fun ping() {
        redisTemplate.executePipelined { action ->
            val conn = action as StringRedisConnection
            val key = generateKey()
            conn.zAdd(key, Instant.now().toEpochMilli().toDouble(), APP_ID)
            conn.zRemRangeByScore(
                key,
                Double.NEGATIVE_INFINITY,
                Instant.now().minusSeconds(PING_INTERVAL_SECONDS * PING_FAILURE_THRESHOLD).toEpochMilli().toDouble()
            )
            null
        }
    }

    @PreDestroy
    fun leave() {
        redisTemplate.opsForZSet().remove(generateKey(), APP_ID)
    }

    // 해당 모듈을 각 마이크로서비스에 독립적으로 붙혔을 때
    // 중재자가 독립적인 키로 동작하게 함
    private fun generateKey(): String
    = "message-relay-coordinator::app-list::%s".format(applicationName)
}