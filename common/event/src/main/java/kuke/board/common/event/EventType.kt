package kuke.board.common.event

import kuke.board.common.event.payload.*
import mu.KotlinLogging

// 이벤트들이 어떤 페이로드를 가지는지 알려줌
enum class EventType(
    // 어떤 페이로드 타입인지
    private val payloadClass: Class<out EventPayload>,
    // 어떤 토픽으로 전달되는지
    private val topic: String
) {
    ARTICLE_CREATED(ArticleCreatedEventPayload::class.java, Topic.KUKE_BOARD_ARTICLE),
    ARTICLE_UPDATED(ArticleUpdatedEventPayload::class.java, Topic.KUKE_BOARD_ARTICLE),
    ARTICLE_DELETED(ArticleDeletedEventPayload::class.java, Topic.KUKE_BOARD_ARTICLE),
    COMMENT_CREATED(CommentCreatedEventPayload::class.java, Topic.KUKE_BOARD_COMMENT),
    COMMENT_DELETED(CommentDeletedEventPayload::class.java, Topic.KUKE_BOARD_COMMENT),
    ARTICLE_LIKED(ArticleLikedEventPayload::class.java, Topic.KUKE_BOARD_LIKE),
    ARTICLE_UNLIKED(ArticleUnlikedEventPayload::class.java, Topic.KUKE_BOARD_LIKE),
    ARTICLE_VIEWED(ArticleViewedEventPayload::class.java, Topic.KUKE_BOARD_VIEW),;

    companion object {
        private val log = KotlinLogging.logger {}

        fun from(type: String): EventType? {
            return try {
                valueOf(type)
            } catch (e: Exception) {
                log.error("[EventType.from] type=$type", e)
                null
            }
        }

        class Topic() {
            companion object {
                val KUKE_BOARD_ARTICLE = "kuke-board-article"
                val KUKE_BOARD_COMMENT = "kuke-board-comment"
                val KUKE_BOARD_LIKE = "kuke-board-like"
                val KUKE_BOARD_VIEW = "kuke-board-view"
            }
        }
    }
}