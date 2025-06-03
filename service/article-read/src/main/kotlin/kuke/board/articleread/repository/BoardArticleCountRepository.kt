package kuke.board.articleread.repository

import kuke.board.articleread.repository.ArticleIdListRepository.Companion
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Repository

@Repository
class BoardArticleCountRepository(
    private val redisTemplate: StringRedisTemplate
) {
    companion object {
        // article-read::board-article-count::board::{boardId}
        private val KEY_FORMAT = "article-read::board-article-count::board::%s"
    }

    fun createOrUpdate(boardId: Long, articleCount: Long) {
        redisTemplate.opsForValue().set(generateKey(boardId), articleCount.toString())
    }

    fun read(boardId: Long): Long? {
        val result = redisTemplate.opsForValue().get(generateKey(boardId))
        return result?.toLong()
    }

    private fun generateKey(boardId: Long): String
    = KEY_FORMAT.format(boardId)
}