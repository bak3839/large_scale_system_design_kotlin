package kuke.board.article

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.ComponentScan
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.scheduling.annotation.EnableScheduling

@EntityScan(basePackages = ["kuke.board"])
@SpringBootApplication
@EnableJpaRepositories(basePackages = ["kuke.board"])
@EnableScheduling
class ArticleApplication

fun main(args: Array<String>) {
    SpringApplication.run(ArticleApplication::class.java, *args)
}