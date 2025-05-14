package kuke.board.view.service

import kuke.board.view.repository.ArticleViewCountRepository
import org.springframework.stereotype.Service

@Service
class ArticleViewService(
    private val articleViewCountRepository: ArticleViewCountRepository,
    private val articleViewCountBackUpProcessor: ArticleViewCountBackUpProcessor
) {
    private val BACK_UP_BATCH_SUZE = 100

    fun increase(articleId: Long, userId: Long): Long? {
        return articleViewCountRepository.increase(articleId)
            ?.also {
                if(it.rem(BACK_UP_BATCH_SUZE) == 0L) {
                    articleViewCountBackUpProcessor.backup(articleId, it)
                }
            }
    }

    fun count(articleId: Long): Long?
    = articleViewCountRepository.read(articleId)
}