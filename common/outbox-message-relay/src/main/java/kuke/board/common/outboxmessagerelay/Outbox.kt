package kuke.board.common.outboxmessagerelay

import jakarta.persistence.*
import kuke.board.common.event.EventType
import java.time.LocalDateTime

@Entity
@Table(name = "outbox")
class Outbox(
    @Id
    private val outboxId: Long,
    @Enumerated(EnumType.STRING)
    private val eventType: EventType,
    private val payload: String,
    private val shardKey: Long,
    private val createdAt: LocalDateTime = LocalDateTime.now(),
) {
    companion object {
        fun create(outboxId: Long, eventType: EventType, payload: String, shardKey: Long): Outbox
        = Outbox(
            outboxId = outboxId,
            eventType = eventType,
            payload = payload,
            shardKey = shardKey
        )
    }
}