package io.flavien.demo.domain.tenant.configuration

import io.flavien.demo.domain.tenant.TenantContext
import io.flavien.demo.domain.tenant.repository.TenantRegistry
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.DisposableBean
import org.springframework.dao.DataAccessException
import org.springframework.data.redis.connection.RedisClusterConnection
import org.springframework.data.redis.connection.RedisConnection
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.RedisPassword
import org.springframework.data.redis.connection.RedisSentinelConnection
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory
import org.springframework.data.redis.connection.jedis.JedisExceptionConverter
import java.util.concurrent.ConcurrentHashMap

class TenantJedisConnectionFactory(
    private val registry: TenantRegistry,
) : RedisConnectionFactory,
    DisposableBean {
    private val factories = ConcurrentHashMap<String, JedisConnectionFactory>()
    private val exceptionConverter = JedisExceptionConverter()

    internal fun getOrCreate(tenantId: String): JedisConnectionFactory =
        factories.computeIfAbsent(tenantId) { id ->
            val tenant = registry.get(id)
            val config =
                RedisStandaloneConfiguration(tenant.redis.host, tenant.redis.port).apply {
                    database = tenant.redis.database
                    tenant.redis.password?.let { password = RedisPassword.of(it) }
                }
            JedisConnectionFactory(config).apply { afterPropertiesSet() }
        }

    private fun resolvedFactory(): JedisConnectionFactory = getOrCreate(TenantContext.require())

    override fun getConnection(): RedisConnection = resolvedFactory().connection

    override fun getClusterConnection(): RedisClusterConnection = resolvedFactory().clusterConnection

    override fun getSentinelConnection(): RedisSentinelConnection = resolvedFactory().sentinelConnection

    override fun getConvertPipelineAndTxResults(): Boolean = true

    override fun translateExceptionIfPossible(ex: RuntimeException): DataAccessException? = exceptionConverter.convert(ex)

    override fun destroy() {
        factories.forEach { (tenantId, factory) ->
            try {
                factory.destroy()
            } catch (ex: Exception) {
                log.warn("Failed to destroy JedisConnectionFactory for tenant $tenantId", ex)
            }
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(TenantJedisConnectionFactory::class.java)
    }
}
