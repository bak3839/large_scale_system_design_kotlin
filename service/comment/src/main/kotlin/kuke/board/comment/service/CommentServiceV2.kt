package kuke.board.comment.service

import kuke.board.comment.entity.ArticleCommentCount
import kuke.board.comment.entity.Comment
import kuke.board.comment.entity.CommentPath
import kuke.board.comment.entity.CommentV2
import kuke.board.comment.repository.ArticleCommentCountRepository
import kuke.board.comment.repository.CommentRepositoryV2
import kuke.board.comment.service.request.CommentCreateRequestV2
import kuke.board.comment.service.response.CommentPageResponse
import kuke.board.comment.service.response.CommentResponse
import kuke.board.common.snowflake.Snowflake
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.function.Predicate

@Service
class CommentServiceV2(
    private val commentRepositoryV2: CommentRepositoryV2,
    private val articleCommentCountRepository: ArticleCommentCountRepository
) {
    private val snowflake = Snowflake()

    @Transactional
    fun create(request: CommentCreateRequestV2): CommentResponse {
        val parent: CommentV2? = findParent(request)
        val parentCommentPath = parent?.let {
            parent.commentPath
        } ?: CommentPath.create("")

        val comment = commentRepositoryV2.save(
            CommentV2.create(
                commentId = snowflake.nextId(),
                content = request.content,
                articleId = request.articleId,
                writerId = request.writerId,
                commentPath = parentCommentPath.createChildCommentPath(
                    commentRepositoryV2.findDescendantsTopPath(request.articleId, parentCommentPath.path)
                )
            )
        )

        val result = articleCommentCountRepository.increase(request.articleId)
        if(result == 0) {
            articleCommentCountRepository.save(
                ArticleCommentCount.init(articleId = request.articleId, commentCount = 1L)
            )
        }
        return CommentResponse.from(comment)
    }

    private fun findParent(request: CommentCreateRequestV2): CommentV2? {
        val parentPath = request.parentPath

        parentPath?.let {
            return commentRepositoryV2.findByPath(parentPath)
                .filter(Predicate.not(CommentV2::deleted))
                .orElseThrow()
        } ?: return null
    }

    fun read(commentId: Long): CommentResponse {
        return CommentResponse.from(
            commentRepositoryV2.findById(commentId).orElseThrow()
        )
    }

    @Transactional
    fun delete(commentId: Long) {
        commentRepositoryV2.findById(commentId)
            .filter(Predicate.not(CommentV2::deleted))
            .ifPresent {
                if (hasChildren(it)) {
                    it.delete()
                } else {
                    delete(it)
                }
            }

    }

    private fun hasChildren(comment: CommentV2): Boolean {
        return commentRepositoryV2.findDescendantsTopPath(
            comment.articleId,
            comment.commentPath.path
        )?.let { true } ?: false
    }

    private fun delete(comment: CommentV2) {
        println("-------------- delete comment ${comment.articleId}")
        commentRepositoryV2.delete(comment)
        articleCommentCountRepository.decrease(comment.articleId)
        // 상위 댓글을 검사해서 deleted = true 이면 삭제 진행
        if (!comment.isRoot()) {
            commentRepositoryV2.findByPath(comment.commentPath.getParentPath())
                .filter(CommentV2::deleted)
                .filter(Predicate.not(this::hasChildren))
                .ifPresent(this::delete)
        }
    }

    fun readAll(articleId: Long, page: Long, pageSize: Long): CommentPageResponse {
        return CommentPageResponse.of(
            commentRepositoryV2.findAll(articleId, (page - 1) * pageSize, pageSize)
                .map(CommentResponse::from),
            commentRepositoryV2.count(articleId, PageCalculator.calculatePageLimit(page, pageSize, 10L))
        )
    }

    fun readAllInfiniteScroll(articleId: Long, lastPath: String?, pageSize: Long): List<CommentResponse> {
        val comments = lastPath?.let {
            commentRepositoryV2.findAllInfiniteScroll(articleId, lastPath, pageSize)
        } ?: commentRepositoryV2.findAllInfiniteScroll(articleId, pageSize)

        return comments.map(CommentResponse::from)
    }

    fun count(articleId: Long): Long
    = articleCommentCountRepository.findById(articleId)
        .map(ArticleCommentCount::commentCount)
        .orElse(0L)
}