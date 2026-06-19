package io.flavien.demo.batch.configuration

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.sql.DataSource

@Configuration
class BatchDataSourceConfiguration {
    @Bean
    fun batchDataSource(
        @Value("\${spring.batch.datasource.url}") url: String,
        @Value("\${spring.batch.datasource.username}") username: String,
        @Value("\${spring.batch.datasource.password}") password: String,
    ): DataSource =
        HikariDataSource(
            HikariConfig().apply {
                this.jdbcUrl = url
                this.username = username
                this.password = password
                poolName = "hikari-batch"
            },
        )
}
