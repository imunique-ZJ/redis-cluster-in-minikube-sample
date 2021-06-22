package org.example

import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.CachingConfigurerSupport
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.MapPropertySource
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.data.redis.connection.RedisClusterConfiguration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory

@Configuration
@EnableCaching
class RedisConfig : CachingConfigurerSupport() {
    private fun connectionFactory(configuration: RedisClusterConfiguration): RedisConnectionFactory {
        val connectionFactory = JedisConnectionFactory(configuration)
        connectionFactory.afterPropertiesSet()
        return connectionFactory
    }

    @Bean
    fun getClusterConfiguration(
        @Value("\${spring.redis.cluster.nodes}") clusterNodes: String,
        @Value("\${spring.redis.cluster.timeout}") timeout: Long,
        @Value("\${spring.redis.cluster.max-redirects}") redirects: Int
    ): RedisClusterConfiguration {
        val source: MutableMap<String, Any> = HashMap()
        source["spring.redis.cluster.nodes"] = clusterNodes
        source["spring.redis.cluster.timeout"] = timeout
        source["spring.redis.cluster.max-redirects"] = redirects
        return RedisClusterConfiguration(MapPropertySource("RedisClusterConfiguration", source))
    }

    @Bean
    fun cacheManager(factory: RedisConnectionFactory): CacheManager {
        return RedisCacheManager.builder(factory).build()
    }
}