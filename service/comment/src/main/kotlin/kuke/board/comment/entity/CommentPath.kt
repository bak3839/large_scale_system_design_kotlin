package kuke.board.comment.entity

import jakarta.persistence.Embeddable
import org.aspectj.weaver.tools.cache.SimpleCacheFactory
import org.aspectj.weaver.tools.cache.SimpleCacheFactory.path

@Embeddable
class CommentPath(
    var path: String
) {
    companion object {
        private val CHARSET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
        private val DEPTH_CHUNK_SIZE= 5
        private val MAX_DEPTH = 5
        // MIN_CHUNK: "00000", MAX_CHUNK: "zzzzz"
        private val MIN_CHUNK = CHARSET[0].toString().repeat(DEPTH_CHUNK_SIZE)
        private val MAX_CHUNK = CHARSET.last().toString().repeat(DEPTH_CHUNK_SIZE)

        fun create(path: String): CommentPath {
            if(isDepthOverflowed(path)) {
                throw IllegalStateException("Depth overflowed")
            }

            val commentPath = CommentPath(path)
            return commentPath
        }

        private fun isDepthOverflowed(path: String): Boolean {
            return calDepth(path) > MAX_DEPTH
        }

        private fun calDepth(path: String): Int {
            return path.length / DEPTH_CHUNK_SIZE
        }
    }

    fun getDepth(): Int = calDepth(path)

    fun isRoot(): Boolean = calDepth(path) == 1

    fun getParentPath(): String = path.substring(0, SimpleCacheFactory.path.length - DEPTH_CHUNK_SIZE)

    fun createChildCommentPath(descendantsTopPath: String?): CommentPath {
        if(descendantsTopPath == null) {
            return create(path + MIN_CHUNK)
        }

        val childrenTopPath = findChildrenTopPath(descendantsTopPath)
        return create(increase(childrenTopPath))
    }

    // 오름차순으로 정렬되기 때문에 가장 큰 path 값이 마지막 댓글
    // 다음 경로를 알기 위해서는 descendantsTopPath 구한 후
    // 댓글을 추가할 계층의 길이만큼 자르고 + 1
    // descendantsTopPath -> childrenTopPath 찾기위해 존재
    private fun findChildrenTopPath(descendantsTopPath: String): String
            = descendantsTopPath.substring(0, (getDepth() + 1) * DEPTH_CHUNK_SIZE)

    private fun increase(path: String): String {
        val lastChunk = path.substring(path.length - DEPTH_CHUNK_SIZE)
        if(isChunkOverflowed(lastChunk)) {
            throw IllegalStateException("chunk overflowed")
        }

        val charsetLength = CHARSET.length
        var value = 0

        for(ch in lastChunk.toCharArray()) {
            value = value * charsetLength + CHARSET.indexOf(ch)
        }
        value += 1

        var result = ""
        for(i in 0 until DEPTH_CHUNK_SIZE) {
            result = CHARSET[value % charsetLength] + result
            value /= charsetLength
        }

        return path.substring(0, path.length - DEPTH_CHUNK_SIZE) + result
    }

    private fun isChunkOverflowed(lastChunk: String): Boolean = MAX_CHUNK == lastChunk
}
