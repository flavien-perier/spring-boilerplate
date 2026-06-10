package io.flavien.demo.domain.configuration

import io.flavien.demo.domain.session.repository.AccessTokenRepository
import io.flavien.demo.domain.session.repository.RefreshTokenRepository
import io.flavien.demo.domain.tenant.configuration.TenantJedisConnectionFactory
import io.flavien.demo.domain.tenant.repository.TenantRegistry
import io.flavien.demo.domain.user.repository.ForgotPasswordRepository
import io.flavien.demo.domain.user.repository.UserActivationRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories
import org.springframework.data.redis.serializer.StringRedisSerializer

@Configuration
@EnableRedisRepositories(
    basePackageClasses = [
        AccessTokenRepository::class,
        RefreshTokenRepository::class,
        ForgotPasswordRepository::class,
        UserActivationRepository::class,
    ],
)
class ValkeyConfiguration {
    @Bean
    @Primary
    fun redisConnectionFactory(registry: TenantRegistry): RedisConnectionFactory = TenantJedisConnectionFactory(registry)

    @Bean
    fun redisTemplate(connectionFactory: RedisConnectionFactory): RedisTemplate<String, Any> {
        val template = RedisTemplate<String, Any>()
        template.connectionFactory = connectionFactory
        template.keySerializer = StringRedisSerializer()
        template.hashKeySerializer = StringRedisSerializer()
        return template
    }
}
