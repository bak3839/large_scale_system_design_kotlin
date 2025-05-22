package kuke.board.common.outboxmessagerelay


import kuke.board.common.event.Event
import kuke.board.common.event.EventPayload
import kuke.board.common.event.EventType
import kuke.board.common.snowflake.Snowflake
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component

@Component
class OutboxEventPublisher(
    private val applicationEventPublisher: ApplicationEventPublisher,
) {
    private val outboxIdSnowflake: Snowflake = Snowflake()
    private val eventIdSnowflake: Snowflake = Snowflake()

    // 해당 클래스로 이벤트를 발행
    fun publish(type: EventType, payload: EventPayload, shardKey: Long) {
        val event = Event(
            eventId = eventIdSnowflake.nextId(),
            type = type,
            payload = payload
        )

        val outbox = Outbox.create(
            outboxId = outboxIdSnowflake.nextId(),
            eventType = type,
            payload = event.toJson(),
            shardKey = shardKey % MessageRelayConstants.SHARD_COUNT
        )
        applicationEventPublisher.publishEvent(OutboxEvent.of(outbox))
    }
}