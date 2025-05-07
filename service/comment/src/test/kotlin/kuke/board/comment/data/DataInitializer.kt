package kuke.board.comment.data

import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import kuke.board.comment.CommentApplication
import kuke.board.comment.entity.Comment
import kuke.board.common.snowflake.Snowflake
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.support.TransactionTemplate
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors

@SpringBootTest(classes = [CommentApplication::class])
class DataInitializer(
) {
    companion object {
        val BULK_INSERT_SIZE = 2000
        val EXECUTE_COUNT = 6000
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
            executorService.submit {
                insert()
                latch.countDown()
                println("latch.getCount() = ${latch.getCount()}")
            }
        }

        latch.await()
        executorService.shutdown()
    }

    fun insert() {
        var prev: Comment? = null

        transactionTemplate.executeWithoutResult {
            for(i in 0 until BULK_INSERT_SIZE) {
                val comment = Comment.create(
                    commentId = snowflake.nextId(),
                    content = "content",
                    parentCommentId = if(i % 2 == 0) null else prev?.commentId,
                    articleId = 1L,
                    writerId = 1L,
                )
                prev = comment
                entityManager.persist(comment)
            }
        }
    }
}