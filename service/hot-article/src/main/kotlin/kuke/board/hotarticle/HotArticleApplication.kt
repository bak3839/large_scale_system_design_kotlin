package kuke.board.article

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class ArticleApplication

fun main(args: Array<String>) {
    SpringApplication.run(ArticleApplication::class.java, *args)
}