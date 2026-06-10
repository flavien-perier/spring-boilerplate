package io.flavien.demo.domain.tenant.configuration

import io.flavien.demo.domain.tenant.repository.TenantRegistry
import jakarta.annotation.PostConstruct
import liquibase.integration.spring.SpringLiquibase
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class LiquibaseMultiTenantRunner(
    private val registry: TenantRegistry,
    private val provider: SchemaMultiTenantConnectionProvider,
) {
    companion object {
        private val log = LoggerFactory.getLogger(LiquibaseMultiTenantRunner::class.java)
    }

    @PostConstruct
    fun runMigrations() {
        registry.getAll().values.forEach { tenant ->
            log.info("Running Liquibase migrations for tenant: ${tenant.tenantId}")
            try {
                val liquibase =
                    SpringLiquibase().apply {
                        dataSource = provider.getOrCreate(tenant.tenantId)
                        defaultSchema = tenant.db.schema
                        changeLog = "classpath:db/changelog/db.changelog-master.yaml"
                        setShouldRun(true)
                    }
                liquibase.afterPropertiesSet()
                log.info("Migrations complete for tenant: ${tenant.tenantId}")
            } catch (e: Exception) {
                log.error("Liquibase migration failed for tenant: ${tenant.tenantId}", e)
                throw IllegalStateException("Liquibase migration failed for tenant: ${tenant.tenantId}", e)
            }
        }
    }
}
