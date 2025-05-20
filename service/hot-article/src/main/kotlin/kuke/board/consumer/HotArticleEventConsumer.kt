package kuke.board.consumer

import kuke.board.common.event.Event
import kuke.board.common.event.EventType
import kuke.board.hotarticle.service.HotArticleService
import mu.KotlinLogging
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component

@Component
class HotArticleEventConsumer(
    private val hotArticleService: HotArticleService,
) {
    private val log = KotlinLogging.logger {}

    @KafkaListener(topics = [
        EventType.Companion.Topic.KUKE_BOARD_ARTICLE,
        EventType.Companion.Topic.KUKE_BOARD_COMMENT,
        EventType.Companion.Topic.KUKE_BOARD_LIKE,
        EventType.Companion.Topic.KUKE_BOARD_VIEW,
    ])
    fun listen(message: String, ack: Acknowledgment) {
        log.info {"[HotArticleEventConsumer.listen] received message=$message]"}
        Event.fromJson(message)?.let {
            hotArticleService.handleEvent(it)
        }

        // 해당 메시지가 잘 처리되었다는 것을 카프카에게 알려줌
        ack.acknowledge()
    }

}