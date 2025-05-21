package kuke.board.common.outboxmessagerelay

import java.util.stream.LongStream

class AssignedShard(
    val shards: List<Long>
) {

    companion object {
        fun of(appId: String, appIds: List<String>, shardCount: Long): AssignedShard
        = AssignedShard(shards = assign(appId, appIds, shardCount))

        private fun assign(appId: String, appIds: List<String>, shardCount: Long): List<Long> {
            val appIndex = findAppIndex(appId, appIds)
            if(appIndex == -1) return listOf()

            val start = appIndex * shardCount / appIds.size
            val end = (appIndex + 1) * shardCount / appIds.size - 1
            return LongStream.rangeClosed(start, end).boxed().toList()
        }

        // appIds: 실행된 애플리케이션 목록을 정렬 상태로 가지고 있음
        // appId: 몇 번째 index 인지 반환
        private fun findAppIndex(appId: String, appIds: List<String>): Int
                = appIds.indexOf(appId)
    }
}