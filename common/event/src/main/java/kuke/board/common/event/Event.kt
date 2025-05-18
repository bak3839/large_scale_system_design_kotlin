package kuke.board.common.event

import kuke.board.common.DataSerializer

class Event<T: EventPayload>(
    val eventId: Long,
    val type: EventType,
    val payload: T
) {
    companion object {
        fun of(eventId: Long, type: EventType, eventPayload: EventPayload): Event<EventPayload> {
            return Event<EventPayload>(
                eventId = eventId,
                type = type,
                payload = eventPayload
            )
        }

        fun fromJson(json: String): Event<EventPayload>? {
            val eventRaw = DataSerializer.deserialize(json, EventRaw::class.java) ?: return null

            val type = EventType.from(eventRaw.type)
            val payload = DataSerializer.deserialize(eventRaw.payload, type.payloadClass)

            return Event(
                eventId = eventRaw.eventId,
                type = type,
                payload = payload
            )
        }

        private data class EventRaw(
            val eventId: Long,
            val type: String,
            val payload: Any
        )
    }
}

fun <T : EventPayload> Event<T>.toJson()
= DataSerializer.serialize(this)