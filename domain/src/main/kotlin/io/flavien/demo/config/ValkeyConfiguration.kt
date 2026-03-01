package io.flavien.demo.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisPassword
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate


@Configuration
class ValkeyConfiguration {

    @Value("\${spring.data.redis.host:127.0.0.1}")
    var host = ""

    @Value("\${spring.data.redis.port:6379}")
    var port = 6379

    @Value("\${spring.data.redis.password:password}")
    var password = ""

    @Bean
    fun jedisConnectionFactory(): JedisConnectionFactory {
        val redisStandaloneConfiguration = RedisStandaloneConfiguration(host, port)
        redisStandaloneConfiguration.password = RedisPassword.of(password)
        return JedisConnectionFactory(redisStandaloneConfiguration)
    }

    @Bean
    fun redisTemplate(): RedisTemplate<String, Any> {
        val template = RedisTemplate<String, Any>()
        template.connectionFactory = jedisConnectionFactory()
        return template
    }
}