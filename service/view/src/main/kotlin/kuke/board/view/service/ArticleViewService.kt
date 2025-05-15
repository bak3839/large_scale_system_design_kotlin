package kuke.board.view.service

import kuke.board.view.repository.ArticleViewCountRepository
import kuke.board.view.repository.ArticleViewDistributedLockRepository
import org.springframework.stereotype.Service
import java.time.Duration

@Service
class ArticleViewService(
    private val articleViewCountRepository: ArticleViewCountRepository,
    private val articleViewCountBackUpProcessor: ArticleViewCountBackUpProcessor,
    private val articleViewDistributedLockRepository: ArticleViewDistributedLockRepository
) {
    private val BACK_UP_BATCH_SUZE = 100
    private val TTL = Duration.ofMinutes(10)

    fun increase(articleId: Long, userId: Long): Long? {
        if(articleViewDistributedLockRepository.lock(articleId, userId, TTL)?.not() == true) {
            return articleViewCountRepository.read(articleId)
        }

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