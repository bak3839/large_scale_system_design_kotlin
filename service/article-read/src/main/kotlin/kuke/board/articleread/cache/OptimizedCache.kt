package kuke.board.articleread.cache

import com.fasterxml.jackson.annotation.JsonIgnore
import kuke.board.common.DataSerializer
import java.time.Duration
import java.time.LocalDateTime


data class OptimizedCache(
    val data: String,
    val expiredAt: LocalDateTime
) {
    companion object {
        fun of(data: Any, ttl: Duration): OptimizedCache
        = OptimizedCache(
            data = DataSerializer.serialize(data)!!,
            expiredAt = LocalDateTime.now().plus(ttl)
        )
    }

    @JsonIgnore
    fun isExpired(): Boolean
    = LocalDateTime.now().isAfter(expiredAt)

    fun <T> parseData(dataType: Class<T>): T
    = DataSerializer.deserialize(data, dataType)!!
}