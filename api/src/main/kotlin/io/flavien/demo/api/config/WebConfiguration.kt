package io.flavien.demo.api.config

import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.server.ResponseStatusException

@Controller
class WebConfiguration {

    @GetMapping("/", "/**/{path:[^\\.]*}")
    fun forward(request: HttpServletRequest): String {
        if (request.requestURI.startsWith("/api")) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND)
        }
        return "forward:/index.html"
    }
}
