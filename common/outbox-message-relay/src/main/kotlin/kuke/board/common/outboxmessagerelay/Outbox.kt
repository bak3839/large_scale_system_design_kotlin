package kuke.board.common.outboxmessagerelay

import jakarta.persistence.*
import kuke.board.common.event.EventType
import java.time.LocalDateTime

@Entity
@Table(name = "outbox")
class Outbox(
    @Id
    val outboxId: Long,
    @Enumerated(EnumType.STRING)
    val eventType: EventType,
    val payload: String,
    val shardKey: Long,
    val createdAt: LocalDateTime = LocalDateTime.now(),
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