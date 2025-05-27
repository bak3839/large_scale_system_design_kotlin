package kuke.board.articleread.repository

import kuke.board.common.DataSerializer
import org.springframework.data.redis.core.StringRedisTemplate
import java.time.Duration

const val KEY_FORMAT = "article-read::article::%s"

class ArticleQueryModelRepository(
    private val redisTemplate: StringRedisTemplate
) {

    fun create(articleQueryModel: ArticleQueryModel, ttl: Duration) {
        redisTemplate.opsForValue()
            .set(generateKey(articleQueryModel), DataSerializer.serialize(articleQueryModel)!!, ttl)
    }

    fun update(articleQueryModel: ArticleQueryModel) {
        redisTemplate.opsForValue()
            .setIfPresent(generateKey(articleQueryModel), DataSerializer.serialize(articleQueryModel)!!)
    }

    fun delete(articleQueryModel: ArticleQueryModel) {
        redisTemplate.delete(generateKey(articleQueryModel))
    }

    fun read(articleId: Long): ArticleQueryModel? {
        val json = redisTemplate.opsForValue().get(generateKey(articleId))
            ?: throw NoSuchElementException("Article with id $articleId not found")
        return DataSerializer.deserialize(json, ArticleQueryModel::class.java)
    }

    private fun generateKey(articleQueryModel: ArticleQueryModel): String
    = generateKey(articleQueryModel.articleId)

    private fun generateKey(articleId: Long): String
    = KEY_FORMAT.format(articleId)
}