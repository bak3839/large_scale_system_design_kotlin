package kuke.board.articleread.repository

import org.springframework.data.domain.Range
import org.springframework.data.redis.connection.Limit
import org.springframework.data.redis.connection.StringRedisConnection
import org.springframework.data.redis.core.RedisCallback
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Repository

@Repository
class ArticleIdListRepository(
    private val redisTemplate: StringRedisTemplate
) {
    companion object {
        // article-read::board::{boardId}::article-list
        private val KEY_FORMAT = "article-read::board::%s::article-list"
    }

    fun add(boardId: Long, articleId: Long, limit: Long) {
        redisTemplate.executePipelined { action ->
            val conn = action as StringRedisConnection
            val key = KEY_FORMAT.format(boardId)
            conn.zAdd(key, 0.0, toPaddedString(articleId))
            conn.zRemRange(key, 0, - limit - 1)
        }
    }

    fun delete(boardId: Long, articleId: Long) {
        redisTemplate.opsForZSet().remove(generateKey(boardId), toPaddedString(articleId))
    }

    fun readAll(boardId: Long, offset:Long, limit: Long): List<Long>
    = redisTemplate.opsForZSet()
        .reverseRange(generateKey(boardId), offset, offset + limit - 1)
        ?.map(String::toLong)
        ?: emptyList()

    fun readAllInfiniteScroll(boardId: Long, lastArticleId: Long?, limit: Long): List<Long>
    = redisTemplate.opsForZSet().reverseRangeByLex(
        generateKey(boardId),
        lastArticleId?.let {
            Range.leftUnbounded(Range.Bound.exclusive(toPaddedString(lastArticleId)))
        } ?: Range.unbounded(),
        Limit.limit().count(limit.toInt()))
        ?.map(String::toLong)
        ?: emptyList()

    private fun toPaddedString(articleId: Long): String
    = "%019d".format(articleId)

    private fun generateKey(boardId: Long): String
    = KEY_FORMAT.format(boardId)
}