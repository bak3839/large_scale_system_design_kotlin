package kuke.board.comment

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@EntityScan(basePackages = ["kuke.board"])
@SpringBootApplication
@EnableJpaRepositories(basePackages = ["kuke.board"])
class CommentApplication

fun main(args: Array<String>) {
    SpringApplication.run(CommentApplication::class.java, *args)
}