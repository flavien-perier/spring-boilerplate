package io.flavien.demo.domain.tenant.configuration

import io.flavien.demo.domain.tenant.model.TenantDefinition
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
            runChangeLog(tenant, "classpath:db/changelog/db.changelog-master.yaml")
            log.info("Migrations complete for tenant: ${tenant.tenantId}")
        }
    }

    private fun runChangeLog(
        tenant: TenantDefinition,
        changeLogPath: String,
    ) {
        try {
            val liquibase =
                SpringLiquibase().apply {
                    dataSource = provider.getOrCreate(tenant.tenantId)
                    defaultSchema = tenant.db.schema
                    changeLog = changeLogPath
                    setShouldRun(true)
                }
            liquibase.afterPropertiesSet()
        } catch (e: Exception) {
            log.error("Liquibase migration failed for tenant: ${tenant.tenantId} ($changeLogPath)", e)
            throw IllegalStateException("Liquibase migration failed for tenant: ${tenant.tenantId} ($changeLogPath)", e)
        }
    }
}
