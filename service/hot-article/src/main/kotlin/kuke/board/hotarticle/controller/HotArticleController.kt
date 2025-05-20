package kuke.board.hotarticle.controller

import kuke.board.hotarticle.service.HotArticleService
import kuke.board.hotarticle.service.response.HotArticleResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/hot-article")
class HotArticleController(
    private val hotArticleService: HotArticleService,
) {

    @GetMapping("/articles/date/{dateStr}")
    fun readAll(
        @PathVariable("dateStr") dateStr: String,
    ): List<HotArticleResponse>
    = hotArticleService.readAll(dateStr)


}