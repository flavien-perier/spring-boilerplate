package io.flavien.demo.api

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["io.flavien.demo"])
class Application

fun main(args: Array<String>) {
	runApplication<Application>(*args)
}
