package io.flavien.demo.domain.tenant.configuration

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.flavien.demo.domain.tenant.repository.TenantRegistry
import org.hibernate.engine.jdbc.connections.spi.AbstractDataSourceBasedMultiTenantConnectionProviderImpl
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.DisposableBean
import java.util.concurrent.ConcurrentHashMap
import javax.sql.DataSource

class SchemaMultiTenantConnectionProvider(
    private val registry: TenantRegistry,
) : AbstractDataSourceBasedMultiTenantConnectionProviderImpl<String>(),
    DisposableBean {
    private val dataSources = ConcurrentHashMap<String, DataSource>()

    internal fun getOrCreate(tenantId: String): DataSource =
        dataSources.computeIfAbsent(tenantId) { id ->
            val tenant = registry.get(id)
            val config =
                HikariConfig().apply {
                    jdbcUrl = tenant.db.jdbcUrl
                    username = tenant.db.username
                    password = tenant.db.password
                    connectionInitSql = """SET search_path TO "${tenant.db.schema}""""
                    minimumIdle = 1
                    maximumPoolSize = 5
                    poolName = "hikari-${tenant.tenantId}"
                }
            HikariDataSource(config)
        }

    override fun selectDataSource(tenantIdentifier: String): DataSource = getOrCreate(tenantIdentifier)

    public override fun selectAnyDataSource(): DataSource {
        if (dataSources.isNotEmpty()) {
            return dataSources.values.first()
        }
        val firstTenant =
            registry.getAll().values.firstOrNull()
                ?: error("No tenants configured — cannot select any DataSource")
        return getOrCreate(firstTenant.tenantId)
    }

    override fun destroy() {
        dataSources.forEach { (tenantId, dataSource) ->
            try {
                (dataSource as? HikariDataSource)?.close()
            } catch (ex: Exception) {
                log.warn("Failed to close DataSource for tenant $tenantId", ex)
            }
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(SchemaMultiTenantConnectionProvider::class.java)
    }
}
