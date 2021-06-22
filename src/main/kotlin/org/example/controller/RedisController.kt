package org.example.controller

import org.example.service.RedisService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController


@RestController
class TestRedisController {
    @Autowired
    private lateinit var redisService: RedisService

    @GetMapping("/{key}")
    fun testCache(@PathVariable key: String = "default"): String {
        return redisService.getName(key)
    }
}