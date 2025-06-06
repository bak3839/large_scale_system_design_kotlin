package kuke.board.articleread.cache

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.stereotype.Component

@Aspect
@Component
class OptimizedCacheAspect(
    private val optimizedCacheManager: OptimizedCacheManager
) {
    @Around("@annotation(OptimizedCacheable)")
    fun around(joinPoint: ProceedingJoinPoint): Any {
        val cacheable = findAnnotation(joinPoint)
        return optimizedCacheManager.process(
            type = cacheable.type,
            ttlSeconds = cacheable.ttlSeconds,
            args = joinPoint.args,
            returnType = findReturnType(joinPoint),
            originDataSupplier = { joinPoint.proceed() }
        )
    }

    private fun findAnnotation(joinPoint: ProceedingJoinPoint): OptimizedCacheable {
        val methodSignature = findMethodSignature(joinPoint)
        return methodSignature.method.getAnnotation(OptimizedCacheable::class.java)
    }

    private fun findReturnType(joinPoint: ProceedingJoinPoint): Class<*> {
        val methodSignature = findMethodSignature(joinPoint)
        return methodSignature.returnType
    }

    private fun findMethodSignature(joinPoint: ProceedingJoinPoint): MethodSignature {
        val signature = joinPoint.signature
        val methodSignature = signature as MethodSignature
        return methodSignature
    }
}