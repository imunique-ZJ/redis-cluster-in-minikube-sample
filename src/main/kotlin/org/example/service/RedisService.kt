package org.example.service

import org.slf4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

@Service
class RedisService {

    @Autowired
    private lateinit var logger: Logger

    @Cacheable(value = ["name"])
    fun getName(key: String): String {
        logger.info("===== cache missed, gonna create value =====")
        logger.info("key: {}", key)
        logger.info("===== value created, will push value into cache =====")
        return key
    }

}