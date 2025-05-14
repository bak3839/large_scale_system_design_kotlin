package kuke.board.view.service

import kuke.board.view.repository.ArticleViewCountRepository
import org.springframework.stereotype.Service

@Service
class ArticleViewService(
    private val articleViewCountRepository: ArticleViewCountRepository
) {
    fun increase(articleId: Long, userId: Long): Long?
    = articleViewCountRepository.increase(articleId)

    fun count(articleId: Long): Long?
    = articleViewCountRepository.read(articleId)
}