package kuke.board.comment

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class CommentApplication

fun main(args: Array<String>) {
    SpringApplication.run(CommentApplication::class.java, *args)
}