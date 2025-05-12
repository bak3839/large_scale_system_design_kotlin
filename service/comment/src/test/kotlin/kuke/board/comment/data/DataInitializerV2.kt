package kuke.board.comment.data

import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import kuke.board.comment.CommentApplication
import kuke.board.comment.entity.Comment
import kuke.board.comment.entity.CommentPath
import kuke.board.comment.entity.CommentPath.Companion
import kuke.board.comment.entity.CommentV2
import kuke.board.common.snowflake.Snowflake
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.support.TransactionTemplate
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors

@SpringBootTest(classes = [CommentApplication::class])
class DataInitializerV2(
) {
    companion object {
        val BULK_INSERT_SIZE = 2000
        val EXECUTE_COUNT = 2000
        private val CHARSET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
        private val DEPTH_CHUNK_SIZE= 5
    }

    @PersistenceContext
    lateinit var entityManager: EntityManager

    @Autowired
    lateinit var transactionTemplate: TransactionTemplate

    val snowflake: Snowflake = Snowflake()
    val latch: CountDownLatch = CountDownLatch(EXECUTE_COUNT)

    @Test
    fun initialize() {
        val executorService = Executors.newFixedThreadPool(10)
        for(i in 0 until EXECUTE_COUNT) {
            val start = i * BULK_INSERT_SIZE
            val end = (i + 1) * BULK_INSERT_SIZE

            executorService.submit {
                insert(start, end)
                latch.countDown()
                println("latch.getCount() = ${latch.getCount()}")
            }
        }

        latch.await()
        executorService.shutdown()
    }

    fun insert(start: Int, end: Int) {
        var prev: Comment? = null

        transactionTemplate.executeWithoutResult {
            for(i in start until end) {
                val comment = CommentV2.create(
                    commentId = snowflake.nextId(),
                    content = "content",
                    articleId = 1L,
                    writerId = 1L,
                    commentPath = toPath(i)
                )
                entityManager.persist(comment)
            }
        }
    }

    fun toPath(n: Int): CommentPath {
        var value = n
        var result = ""

        for(i in 0 until DEPTH_CHUNK_SIZE) {
            result = CHARSET[value % CHARSET.length] + result
            value /= CHARSET.length
        }

        return CommentPath.create(result)
    }
}