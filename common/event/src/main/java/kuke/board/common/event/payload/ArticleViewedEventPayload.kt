package kuke.board.common.event.payload

import kuke.board.common.event.EventPayload

class ArticleViewedEventPayload(
    private val articleId: Long,
    private val articleViewCount: Long,
): EventPayload {
}