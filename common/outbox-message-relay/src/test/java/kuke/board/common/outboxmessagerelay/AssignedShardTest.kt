package kuke.board.common.outboxmessagerelay

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class AssignedShardTest {
    @Test
    fun ofTest() {
        // given
        val shardCount = 64L
        val appList = listOf("appId1", "appId2", "appId3")

        // when
        val assignedShard1 = AssignedShard.of(appList[0], appList, shardCount)
        val assignedShard2 = AssignedShard.of(appList[1], appList, shardCount)
        val assignedShard3 = AssignedShard.of(appList[2], appList, shardCount)
        val assignedShard4 = AssignedShard.of("invalid", appList, shardCount)

        // then
        val result = listOf(
            assignedShard1.shards, assignedShard2.shards,
            assignedShard3.shards, assignedShard4.shards
        ).flatten()

        // then
        assertSoftly(result) { shards ->
            shards.size shouldBe shardCount.toInt()
            shards.forEachIndexed { index, shard ->
                shard shouldBe index.toLong()
            }
        }
        assignedShard4.shards.shouldBeEmpty()
    }
}