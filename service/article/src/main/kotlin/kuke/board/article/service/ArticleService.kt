package kuke.board.article.service

import kuke.board.article.entity.Article
import kuke.board.article.repository.ArticleRepository
import kuke.board.article.service.request.ArticleCreateRequest
import kuke.board.article.service.request.ArticleUpdateRequest
import kuke.board.article.service.response.ArticlePageResponse
import kuke.board.article.service.response.ArticleResponse
import kuke.board.common.snowflake.Snowflake
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ArticleService(
    private val articleRepository: ArticleRepository
) {
    private val snowflake: Snowflake = Snowflake()

    @Transactional
    fun create(request: ArticleCreateRequest): ArticleResponse {
        val article = articleRepository.save(
            Article(
                snowflake.nextId(),
                request.title,
                request.content,
                request.boardId,
                request.writerId
            )
        )

        return ArticleResponse.from(article)
    }

    @Transactional
    fun update(articleId: Long, request: ArticleUpdateRequest): ArticleResponse {
        val article = articleRepository.findById(articleId).orElseThrow()
        article.update(request.title, request.content)
        return ArticleResponse.from(article)
    }

    fun read(articleId: Long): ArticleResponse
    = ArticleResponse.from(articleRepository.findById(articleId).orElseThrow())

    @Transactional
    fun delete(articleId: Long) {
        articleRepository.deleteById(articleId)
    }

    fun readAll(boardId: Long, page: Long, pageSize: Long) :ArticlePageResponse {
        return ArticlePageResponse.of(
            articleRepository.findAll(boardId, (page - 1) * pageSize, pageSize)
                .map(ArticleResponse::from)
                .toList(),
            articleRepository.count(
                boardId,
                PageCalculator.calculatePageLimit(page, pageSize, 10L)
            )
        )
    }
}