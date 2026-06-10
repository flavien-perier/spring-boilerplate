package io.flavien.demo.domain.tenant

import io.flavien.demo.domain.configuration.properties.TenantProperties
import io.flavien.demo.domain.tenant.exception.TenantNotFoundException
import io.flavien.demo.domain.tenant.repository.TenantRegistry
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

class TenantRegistryTest {
    @TempDir
    lateinit var tempDir: File

    private fun tenantYaml(
        id: String,
        schema: String,
    ) = """
        tenantId: $id
        db:
          jdbcUrl: jdbc:postgresql://localhost:5432/mydb
          username: user
          password: secret
          schema: $schema
        redis:
          host: localhost
          port: 6379
          database: 0
        smtp:
          host: smtp.example.com
          port: 587
          username: user@example.com
          password: smtppass
          auth: true
          starttls: true
          accountCreator: no-reply@example.com
          domainLinks: https://example.com:443
        """.trimIndent()

    private fun buildRegistry(dir: File = tempDir): TenantRegistry {
        val props = TenantProperties(directory = dir.absolutePath)
        return TenantRegistry(props).also { it.load() }
    }

    @Test
    fun `loads two valid YAML files and getAll returns both`() {
        File(tempDir, "alpha.yml").writeText(tenantYaml("alpha", "schema_alpha"))
        File(tempDir, "beta.yaml").writeText(tenantYaml("beta", "schema_beta"))

        val registry = buildRegistry()

        assertThat(registry.getAll()).hasSize(2)
        assertThat(registry.getAll()).containsKey("alpha")
        assertThat(registry.getAll()).containsKey("beta")
    }

    @Test
    fun `get returns the correct tenant definition`() {
        File(tempDir, "alpha.yml").writeText(tenantYaml("alpha", "schema_alpha"))

        val registry = buildRegistry()
        val tenant = registry.get("alpha")

        assertThat(tenant.tenantId).isEqualTo("alpha")
        assertThat(tenant.db.schema).isEqualTo("schema_alpha")
        assertThat(tenant.db.jdbcUrl).isEqualTo("jdbc:postgresql://localhost:5432/mydb")
        assertThat(tenant.smtp.host).isEqualTo("smtp.example.com")
        assertThat(tenant.smtp.accountCreator).isEqualTo("no-reply@example.com")
    }

    @Test
    fun `get on unknown tenantId throws TenantNotFoundException`() {
        val registry = buildRegistry()

        assertThatThrownBy { registry.get("nonexistent") }
            .isInstanceOf(TenantNotFoundException::class.java)
            .hasMessageContaining("nonexistent")
    }

    @Test
    fun `empty directory results in empty getAll without exception`() {
        val registry = buildRegistry()

        assertThat(registry.getAll()).isEmpty()
    }

    @Test
    fun `non-existent directory results in empty getAll without exception`() {
        val props = TenantProperties(directory = "/does/not/exist/at/all")
        val registry = TenantRegistry(props)
        registry.load()

        assertThat(registry.getAll()).isEmpty()
    }

    @Test
    fun `malformed YAML file causes load to throw a descriptive exception`() {
        File(tempDir, "bad.yml").writeText("this: is: not: valid: yaml: [")

        val props = TenantProperties(directory = tempDir.absolutePath)
        val registry = TenantRegistry(props)

        assertThatThrownBy { registry.load() }
            .isInstanceOf(com.fasterxml.jackson.core.JacksonException::class.java)
            .message()
            .isNotBlank()
    }
}
