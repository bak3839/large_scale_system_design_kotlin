package kuke.board.articleread.cache

@FunctionalInterface
fun interface OptimizedCacheOriginDataSupplier<T> {
    fun get(): T
}