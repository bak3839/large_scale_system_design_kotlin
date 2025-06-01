package kuke.board.articleread.consumer

import kuke.board.articleread.service.ArticleReadService
import kuke.board.common.event.Event
import kuke.board.common.event.EventType
import mu.KotlinLogging
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component

@Component
class ArticleReadEventConsumer(
    private val articleReadService: ArticleReadService
) {
    private val log = KotlinLogging.logger {}

    @KafkaListener(topics = [
        EventType.Companion.Topic.KUKE_BOARD_ARTICLE,
        EventType.Companion.Topic.KUKE_BOARD_COMMENT,
        EventType.Companion.Topic.KUKE_BOARD_LIKE,
        EventType.Companion.Topic.KUKE_BOARD_VIEW
    ])
    fun listen(message: String, ack: Acknowledgment) {
        log.info { "[ArticleReadEventConsumer.listen] Received message: $message" }
        Event.fromJson(message)?.let {
            articleReadService.handleEvent(it)
        }

        ack.acknowledge()
    }
}