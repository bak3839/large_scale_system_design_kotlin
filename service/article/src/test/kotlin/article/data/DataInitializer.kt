package article.data

import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import kuke.board.article.ArticleApplication
import kuke.board.article.entity.Article
import kuke.board.common.snowflake.Snowflake
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.support.TransactionTemplate
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors

@SpringBootTest(classes = [ArticleApplication::class])
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
        transactionTemplate.executeWithoutResult {
            for(i in 0 until BULK_INSERT_SIZE) {
                entityManager.persist(
                    Article(
                        articleId = snowflake.nextId(),
                        title = "title $i",
                        content = "content $i",
                        boardId = 1L,
                        writerId = 1L))
            }
        }
    }
}