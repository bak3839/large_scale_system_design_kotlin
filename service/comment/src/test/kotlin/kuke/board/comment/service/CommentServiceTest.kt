package kuke.board.comment.service

import io.mockk.impl.annotations.MockK
import kuke.board.comment.repository.CommentRepository
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class CommentServiceTest {
    @InjectMocks
    lateinit var commentService: CommentService

    @MockK
    lateinit var commentRepository: CommentRepository


}