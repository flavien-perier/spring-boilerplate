package io.flavien.demo.batch

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["io.flavien.demo"])
class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
