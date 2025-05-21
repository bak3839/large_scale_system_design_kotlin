package kuke.board.common.outboxmessagerelay

class OutboxEvent(
    private val outbox: Outbox
) {
    companion object {
        fun of(outbox: Outbox): OutboxEvent
        = OutboxEvent(outbox)
    }
}