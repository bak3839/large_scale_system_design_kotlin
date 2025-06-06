package kuke.board.articleread.cache

import kuke.board.common.DataSerializer
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component
import javax.xml.crypto.Data

@Component
class OptimizedCacheManager(
    private val redisTemplate: StringRedisTemplate,
    private val optimizedCacheLockProvider: OptimizedCacheLockProvider
) {
    companion object {
        private const val DELIMITER = "::"
    }

    fun process(type: String, ttlSeconds: Long, args: Array<Any>, returnType: Class<*>, originDataSupplier: OptimizedCacheOriginDataSupplier<*>): Any {
        val key = generateKey(type, args)

        val cachedData = redisTemplate.opsForValue().get(key)
            ?: return refresh(originDataSupplier, key, ttlSeconds)

        val optimizedCache = DataSerializer.deserialize(cachedData, OptimizedCache::class.java)
            ?: return refresh(originDataSupplier, key, ttlSeconds)

        if(!optimizedCache.isExpired()) {
            return optimizedCache.parseData(returnType)
        }

        if(!optimizedCacheLockProvider.lock(key)) {
            return optimizedCache.parseData(returnType)
        }

        try {
            return refresh(originDataSupplier, key, ttlSeconds)
        } finally {
            optimizedCacheLockProvider.unlock(key)
        }
    }

    private fun refresh(originDataSupplier: OptimizedCacheOriginDataSupplier<*>, key: String, ttlSeconds: Long): Any {
        val result = originDataSupplier.get()

        val optimizedCacheTTL = OptimizedCacheTTL.of(ttlSeconds)
        val optimizedCache = OptimizedCache.of(
            data = result!!,
            ttl = optimizedCacheTTL.logicalTTL
        )

        redisTemplate.opsForValue()
            .set(
                key,
                DataSerializer.serialize(optimizedCache)!!,
                optimizedCacheTTL.physicalTTL
            )

        return result
    }

    private fun generateKey(prefix: String, args: Array<Any>): String
    = prefix + DELIMITER + args.joinToString(DELIMITER, transform = Any::toString)
}