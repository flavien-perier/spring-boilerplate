package io.flavien.demo.domain.configuration

import io.flavien.demo.domain.tenant.configuration.SchemaMultiTenantConnectionProvider
import io.flavien.demo.domain.tenant.configuration.TenantContextIdentifierResolver
import io.flavien.demo.domain.tenant.repository.TenantRegistry
import io.flavien.demo.domain.user.entity.User
import io.flavien.demo.domain.user.repository.UserRepository
import org.springframework.boot.persistence.autoconfigure.EntityScan
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.DependsOn
import org.springframework.context.annotation.Primary
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.data.repository.config.BootstrapMode
import org.springframework.jdbc.datasource.AbstractDataSource
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.JpaVendorAdapter
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.orm.jpa.persistenceunit.PersistenceUnitPostProcessor
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter
import org.springframework.transaction.PlatformTransactionManager
import java.sql.Connection
import javax.sql.DataSource

@Configuration
@EntityScan(basePackageClasses = [User::class])
@EnableJpaRepositories(
    basePackageClasses = [UserRepository::class],
    bootstrapMode = BootstrapMode.LAZY,
)
class JpaConfiguration {
    /**
     * Wraps selectAnyDataSource() in a proxy so bean creation never blocks on tenant config.
     * Connections are resolved lazily, which is safe: at runtime tenants are always loaded
     * before any caller requests a connection (Spring Batch JobRepository, schema validation, etc.).
     */
    @Bean
    fun dataSource(provider: SchemaMultiTenantConnectionProvider): DataSource =
        object : AbstractDataSource() {
            override fun getConnection(): Connection = provider.selectAnyDataSource().getConnection()

            override fun getConnection(
                username: String,
                password: String,
            ): Connection = provider.selectAnyDataSource().getConnection(username, password)
        }

    @Bean
    fun multiTenantConnectionProvider(registry: TenantRegistry): SchemaMultiTenantConnectionProvider =
        SchemaMultiTenantConnectionProvider(registry)

    @Bean
    fun currentTenantIdentifierResolver(): TenantContextIdentifierResolver = TenantContextIdentifierResolver()

    @Bean
    fun jpaVendorAdapter(): JpaVendorAdapter = HibernateJpaVendorAdapter()

    @Bean
    @Primary
    @DependsOn("liquibaseMultiTenantRunner")
    fun entityManagerFactory(
        provider: SchemaMultiTenantConnectionProvider,
        resolver: TenantContextIdentifierResolver,
        jpaVendorAdapter: JpaVendorAdapter,
    ): LocalContainerEntityManagerFactoryBean =
        LocalContainerEntityManagerFactoryBean().apply {
            this.jpaVendorAdapter = jpaVendorAdapter
            setPackagesToScan("io.flavien.demo.domain")
            setPersistenceUnitPostProcessors(
                PersistenceUnitPostProcessor { pud ->
                    pud.addManagedClassName(User::class.java.name)
                },
            )
            setJpaPropertyMap(
                mapOf(
                    "hibernate.multi_tenant_connection_provider" to provider,
                    "hibernate.tenant_identifier_resolver" to resolver,
                    "hibernate.dialect" to "org.hibernate.dialect.PostgreSQLDialect",
                    "hibernate.temp.use_jdbc_metadata_defaults" to "false",
                ),
            )
        }

    @Bean
    @Primary
    fun transactionManager(entityManagerFactory: jakarta.persistence.EntityManagerFactory): PlatformTransactionManager =
        JpaTransactionManager().apply { this.entityManagerFactory = entityManagerFactory }
}
