package kuke.board.view.service

import kuke.board.view.entity.ArticleViewCount
import kuke.board.view.repository.ArticleViewCountBackUpRepository
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Component
class ArticleViewCountBackUpProcessor(
    private val articleViewCountBackUpRepository: ArticleViewCountBackUpRepository
) {

    @Transactional
    fun backup(articleId: Long, viewCount: Long) {
        val result = articleViewCountBackUpRepository.updateViewCount(articleId, viewCount)

        if(result == 0) {
            articleViewCountBackUpRepository.findByArticleId(articleId)
                ?: articleViewCountBackUpRepository.save(
                    ArticleViewCount.init(articleId, viewCount)
                )
        }
    }
}