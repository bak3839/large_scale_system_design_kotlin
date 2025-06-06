package kuke.board.articleread.cache

import java.lang.annotation.ElementType
import java.lang.annotation.RetentionPolicy

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class OptimizedCacheable(
    val type: String,
    val ttlSeconds: Long
)
