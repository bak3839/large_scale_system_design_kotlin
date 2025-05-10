package kuke.board.comment.entity

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class CommentPathTest {
    @Test
    fun createChildCommentTest() {
        // 00000 생성
        createChildCommentTest(CommentPath.create(""), null, "00000")

        // 00000
        //      00000 생성
        createChildCommentTest(CommentPath.create("00000"), null, "0000000000")

        // 00000
        // 00001 생성
        createChildCommentTest(CommentPath.create(""), "00000", "00001")

        // 0000z
        //      abcdz
        //           zzzzz
        //                zzzzz
        //      abce0 생성
        createChildCommentTest(CommentPath.create("0000z"), "0000zabcdzzzzzzzzzzz", "0000zabce0")
    }

    fun createChildCommentTest(commentPath: CommentPath, descendantsTopPath: String?, expectedChildPath: String) {
        val childCommentPath = commentPath.createChildCommentPath(descendantsTopPath)
        assertEquals(expectedChildPath, childCommentPath.path)
    }

    @Test
    fun createChildCommentPathInMaxDepthTest() {
        assertThrows(
            IllegalStateException::class.java
        ) {
            CommentPath.create("zzzzz".repeat(5)).createChildCommentPath(null)
        }
    }

    @Test
    fun createChildCommentPathIfChunkOverflowTest() {
        val commentPath = CommentPath.create("")

        // when, then
        assertThrows<IllegalStateException> {
            commentPath.createChildCommentPath("zzzzz")
        }
    }
}