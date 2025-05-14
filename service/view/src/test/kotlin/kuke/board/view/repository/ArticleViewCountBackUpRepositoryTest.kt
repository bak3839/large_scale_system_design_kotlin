package kuke.board.view.repository

import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import kuke.board.view.ViewApplication
import kuke.board.view.entity.ArticleViewCount
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@SpringBootTest(classes = [ViewApplication::class])
class ArticleViewCountBackUpRepositoryTest {
    @Autowired
    lateinit var articleViewCountBackUpRepository: ArticleViewCountBackUpRepository

    @PersistenceContext
    lateinit var entityManager: EntityManager

    @Test
    @Transactional
    fun updateViewCountTest() {
        // given
        articleViewCountBackUpRepository.save(
            ArticleViewCount.init(1L, 0L)
        )
        entityManager.flush()
        entityManager.clear()

        // when
        val result1 = articleViewCountBackUpRepository.updateViewCount(1L, 100L)
        val result2 = articleViewCountBackUpRepository.updateViewCount(1L, 300L)
        val result3 = articleViewCountBackUpRepository.updateViewCount(1L, 200L)

        // then
        assertEquals(result1, 1)
        assertEquals(result2, 1)
        assertEquals(result3, 0)

        val articleViewCount = articleViewCountBackUpRepository.findById(1L).get()
        assertEquals(300L, articleViewCount.viewCount)
    }
}