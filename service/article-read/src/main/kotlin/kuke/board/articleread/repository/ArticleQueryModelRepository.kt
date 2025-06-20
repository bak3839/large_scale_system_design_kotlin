package kuke.board.articleread.repository

import kuke.board.common.DataSerializer
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Repository
import java.time.Duration
import java.util.Collections
import java.util.Objects

@Repository
class ArticleQueryModelRepository(
    private val redisTemplate: StringRedisTemplate
) {
    companion object {
        private val KEY_FORMAT = "article-read::article::%s"
    }

    fun create(articleQueryModel: ArticleQueryModel, ttl: Duration) {
        redisTemplate.opsForValue()
            .set(generateKey(articleQueryModel), DataSerializer.serialize(articleQueryModel)!!, ttl)
    }

    fun update(articleQueryModel: ArticleQueryModel) {
        redisTemplate.opsForValue()
            .setIfPresent(generateKey(articleQueryModel), DataSerializer.serialize(articleQueryModel)!!)
    }

    fun delete(articleId: Long) {
        redisTemplate.delete(generateKey(articleId))
    }

    fun read(articleId: Long): ArticleQueryModel? {
        val json = redisTemplate.opsForValue().get(generateKey(articleId))
            ?: return null
        return DataSerializer.deserialize(json, ArticleQueryModel::class.java)
    }

    private fun generateKey(articleQueryModel: ArticleQueryModel): String
    = generateKey(articleQueryModel.articleId)

    private fun generateKey(articleId: Long): String
    = KEY_FORMAT.format(articleId)

    fun readAll(articleIds: List<Long>): Map<Long, ArticleQueryModel> {
        val keyList = articleIds.map(this::generateKey)
        return redisTemplate.opsForValue().multiGet(keyList)!!
            .filterNotNull()
            .map { json -> DataSerializer.deserialize(json, ArticleQueryModel::class.java)!!}
            .associateBy { it.articleId }
    }
}