package article.repository

import kuke.board.article.ArticleApplication
import kuke.board.article.repository.ArticleRepository
import lombok.extern.slf4j.Slf4j
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@Slf4j
@SpringBootTest(classes = [ArticleApplication::class])
class ArticleRepositoryTest {
    @Autowired
    lateinit var articleRepository: ArticleRepository

    @Test
    fun findAllTest() {
        val articles = articleRepository.findAll(1L, 1499970L, 30L)
        for(article in articles){
            println("article = ${article}")
        }
    }

    @Test
    fun countTest() {
        val count = articleRepository.count(1L, 10000L)
        println("count = $count")
    }

    @Test
    fun findAllInfiniteScrollTest() {
        val articles = articleRepository.findAllInfiniteScroll(1L, 30L)
        for(article in articles){
            println("article = ${article}")
        }

        val lastArticleId = articles.last().articleId
        val articles2 = articleRepository.findAllInfiniteScroll(1L,  30L, lastArticleId)
        for(article2 in articles2){
            println("article2 = ${article2}")
        }
    }
}