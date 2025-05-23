package kuke.board.common.outboxmessagerelay

import mu.KotlinLogging
import org.springframework.data.domain.Pageable
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.scheduling.annotation.Async
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

@Component
class MessageRelay(
    private val outboxRepository: OutboxRepository,
    private val messageRelayCoordinator: MessageRelayCoordinator,
    private val messageRelayKafkaTemplate: KafkaTemplate<String, String>
) {
    private val log = KotlinLogging.logger {}

    // 커밋되기 전
    // 비지니스 로직이 수행되고 단일 트랜잭션에 묶인 상태
    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    fun createOutbox(outboxEvent: OutboxEvent) {
        log.info { "[MessageRelay.createOutbox] outboxEvent=$outboxEvent]" }
        outboxRepository.save(outboxEvent.outbox)
    }

    // 트랜잭션 커밋 후 비동기로 카프카 이벤트 전송
    @Async("messageRelayPublishEventExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun publishEvent(outboxEvent: OutboxEvent) {
        publishEvent(outboxEvent.outbox)
    }

    private fun publishEvent(outbox: Outbox) {
        try {
            messageRelayKafkaTemplate.send(
                outbox.eventType.topic,
                // 카프카에 전송하는 키
                // 동일한 카프카 파티션으로 전송되면 순서가 보장
                // outbox 동일한 파티션에서 순서대로 처리
                outbox.shardKey.toString(),
                outbox.payload
            ).get(1, TimeUnit.SECONDS)
            outboxRepository.delete(outbox)
        } catch (e: Exception) {
            log.error(e) { "[MessageRelay.publishEvent] outbox=$outbox" }
        }
    }

    @Scheduled(
        fixedDelay = 10,
        initialDelay = 5,
        timeUnit = TimeUnit.SECONDS,
        scheduler = "messageRelayPublishPendingEventExecutor"
    )
    fun publishPendingEvent() {
        val assignShards = messageRelayCoordinator.assignShards()
        //log.info { "[MessageRelay.publishPendingEvent] assignedShard size=${assignShards.shards.size}" }
        assignShards.shards.forEach { shard ->
            val outboxes = outboxRepository.findAllByShardKeyAndCreatedAtLessThanEqualOrderByCreatedAtAsc(
                    shard,
                    LocalDateTime.now().minusSeconds(10), // 10초 지난
                    Pageable.ofSize(100)
            )

            outboxes.forEach { outbox ->
                publishEvent(outbox)
            }
        }
    }
}