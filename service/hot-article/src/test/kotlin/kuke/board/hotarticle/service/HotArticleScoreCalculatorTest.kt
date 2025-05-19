package kuke.board.hotarticle.service

import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import kuke.board.hotarticle.repository.ArticleCommentCountRepository
import kuke.board.hotarticle.repository.ArticleLikeCountRepository
import kuke.board.hotarticle.repository.ArticleViewCountRepository
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.random.RandomGenerator

@ExtendWith(MockKExtension::class)
class HotArticleScoreCalculatorTest {
    @MockK
    lateinit var articleLikeCountRepository: ArticleLikeCountRepository

    @MockK
    lateinit var articleViewCountRepository: ArticleViewCountRepository

    @MockK
    lateinit var articleCommentCountRepository: ArticleCommentCountRepository

    @InjectMockKs
    lateinit var hotArticleScoreCalculator: HotArticleScoreCalculator

    @Test
    fun calculateTest() {
        // given
        val articleId = 1L
        val likeCount = RandomGenerator.getDefault().nextLong(100)
        val commentCount = RandomGenerator.getDefault().nextLong(100)
        val viewCount = RandomGenerator.getDefault().nextLong(100)

        every { articleLikeCountRepository.read(articleId) } returns likeCount
        every { articleViewCountRepository.read(articleId) } returns viewCount
        every { articleCommentCountRepository.read(articleId) } returns commentCount

        // when
        val score = hotArticleScoreCalculator.calculate(articleId)

        // then
        score shouldBe (3 * likeCount + 2 * commentCount + 1 * viewCount)
    }
}