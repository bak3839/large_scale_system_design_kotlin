package kuke.board.article.service

object PageCalculator {
    fun calculatePageLimit(page: Long, pageSize: Long, movablePageCount: Long): Long {
        return (((page - 1) / movablePageCount) + 1) * pageSize * movablePageCount + 1
    }
}