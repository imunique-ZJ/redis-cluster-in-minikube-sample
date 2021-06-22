package org.example

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class RedisSampleApp

fun main(args: Array<String>) {
    SpringApplication.run(RedisSampleApp::class.java, *args)
}