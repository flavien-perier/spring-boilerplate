package io.flavien.demo.domain.tenant.repository

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import io.flavien.demo.domain.configuration.properties.TenantProperties
import io.flavien.demo.domain.tenant.exception.TenantNotFoundException
import io.flavien.demo.domain.tenant.model.TenantDefinition
import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.io.File

@Component
class TenantRegistry(
    private val properties: TenantProperties,
) {
    private val mapper =
        ObjectMapper(YAMLFactory()).apply {
            findAndRegisterModules()
        }
    private var tenants: Map<String, TenantDefinition> = emptyMap()

    @PostConstruct
    fun load() {
        val dir = File(properties.directory)
        if (!dir.exists() || !dir.isDirectory) {
            log.warn("Tenant directory not found or not a directory: ${properties.directory}")
            return
        }
        val files =
            dir
                .listFiles { f -> f.extension == "yml" || f.extension == "yaml" }
                ?.sortedBy { it.name }
                ?: emptyList()
        if (files.isEmpty()) {
            log.warn("Tenant directory is empty: ${properties.directory}")
            return
        }
        val loaded = linkedMapOf<String, TenantDefinition>()
        files.forEach { file ->
            val content = resolveEnvVars(file.readText())
            val definition = mapper.readValue(content, TenantDefinition::class.java)
            loaded[definition.tenantId] = definition
            log.info("Loaded tenant: ${definition.tenantId} (schema=${definition.db.schema})")
        }
        tenants = loaded.toMap()
        log.info("Loaded ${tenants.size} tenant(s) from ${properties.directory}")
    }

    fun getAll(): Map<String, TenantDefinition> = tenants

    fun get(tenantId: String): TenantDefinition = tenants[tenantId] ?: throw TenantNotFoundException(tenantId)

    private fun resolveEnvVars(content: String): String {
        val pattern = Regex("""\$\{([^}:]+)(?::([^}]*))?\}""")
        return pattern.replace(content) { match ->
            val varName = match.groupValues[1]
            val default = match.groups[2]?.value
            System.getenv(varName)
                ?: default
                ?: error("Missing environment variable $varName and no default provided")
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(TenantRegistry::class.java)
    }
}
