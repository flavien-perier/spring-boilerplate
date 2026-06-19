package io.flavien.demo.batch.configuration

import jakarta.annotation.PostConstruct
import org.springframework.batch.core.configuration.support.JdbcDefaultBatchConfiguration
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import org.springframework.jdbc.datasource.DataSourceTransactionManager
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator
import org.springframework.transaction.PlatformTransactionManager
import javax.sql.DataSource

@Configuration
class BatchJobRepositoryConfiguration(
    @param:Qualifier("batchDataSource") private val batchDataSource: DataSource,
) : JdbcDefaultBatchConfiguration() {
    private val batchTransactionManager: DataSourceTransactionManager =
        DataSourceTransactionManager(batchDataSource).also { it.afterPropertiesSet() }

    @PostConstruct
    fun initializeBatchSchema() {
        ResourceDatabasePopulator(
            ClassPathResource("org/springframework/batch/core/schema-postgresql.sql"),
        ).also { it.setContinueOnError(true) }.execute(batchDataSource)
    }

    override fun getDataSource(): DataSource = batchDataSource

    override fun getTransactionManager(): PlatformTransactionManager = batchTransactionManager
}
