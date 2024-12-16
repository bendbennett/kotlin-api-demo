package net.synaptology.kotlin_api_demo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["net.synaptology.kotlin_api_demo"])
class KotlinApiDemoApplication

fun main(args: Array<String>) {
    runApplication<KotlinApiDemoApplication>(*args)
}
