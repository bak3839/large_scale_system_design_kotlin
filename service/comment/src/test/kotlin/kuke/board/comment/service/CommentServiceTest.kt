package kuke.board.comment.service

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import kuke.board.comment.entity.Comment
import kuke.board.comment.repository.CommentRepository
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.*

@ExtendWith(MockKExtension::class)
class CommentServiceTest {
    @InjectMockKs
    lateinit var commentService: CommentService

    @RelaxedMockK
    lateinit var commentRepository: CommentRepository

    @Test
    fun `삭제할 댓글이 자식이 있으면, 삭제 표시`() {
        // given
        val articleId = 1L
        val commentId = 2L

        val comment = createComment(articleId, commentId)
        every { commentRepository.findById(any()) } returns Optional.of(comment)
        every { commentRepository.countBy(any(), any(), any()) } returns 2L

        // when
        commentService.delete(commentId)

        // then
        verify {
            comment.delete()
        }
    }

    @Test
    fun `하위 댓글이 삭제되고, 삭제되지 않은 부모면, 하위 댓글만 삭제`() {
        // given
        val articleId = 1L
        val commentId = 2L
        val parentCommentId = 1L

        val comment = createComment(articleId, commentId, parentCommentId)
        every { comment.isRoot() } returns false // 자식 댓글

        val parentComment = mockk<Comment>(relaxed = true)
        every { parentComment.deleted } returns false // 삭제되지 않음

        // 자식 댓글은 자식 댓글이 없으므로 바로 삭제
        every { commentRepository.findById(commentId) } returns Optional.of(comment)
        every { commentRepository.countBy(articleId, commentId, 2L) } returns 1L
        every { commentRepository.findById(parentCommentId) } returns Optional.of(parentComment)

        // when
        commentService.delete(commentId)

        // then
        verify {
            commentRepository.delete(comment)
        }
        verify(exactly = 0) {
            commentRepository.delete(parentComment)
        }
    }

    @Test
    fun `하위 댓글이 삭제되고, 삭제된 부모면, 재귀적으로 모두 삭제`() {
        // given
        val articleId = 1L
        val commentId = 2L
        val parentCommentId = 1L

        val comment = createComment(articleId, commentId, parentCommentId)
        every { comment.isRoot() } returns false // 자식 댓글

        val parentComment = createComment(articleId, parentCommentId)
        every { parentComment.isRoot() } returns true // 부모 댓글
        every { parentComment.deleted } returns true // 삭제됨

        // 자식 댓글이 없으므로 재귀적으로 삭제
        every { commentRepository.findById(commentId) } returns Optional.of(comment)
        every { commentRepository.countBy(articleId, commentId, 2L) } returns 1L

        every { commentRepository.findById(parentCommentId) } returns Optional.of(parentComment)
        every { commentRepository.countBy(articleId, parentCommentId, 2L) } returns 1L

        // when
        commentService.delete(commentId)

        // then
        verify {
            commentRepository.delete(comment)
            commentRepository.delete(parentComment)
        }
    }

    private fun createComment(articleId: Long, commentId: Long): Comment {
        val comment = mockk<Comment>(relaxed = true)
        every { comment.articleId } returns articleId
        every { comment.commentId } returns commentId
        return comment
    }

    private fun createComment(articleId: Long, commentId: Long, parentCommentId: Long): Comment {
        val comment = createComment(articleId, commentId)
        every { comment.parentCommentId } returns parentCommentId
        return comment
    }
}