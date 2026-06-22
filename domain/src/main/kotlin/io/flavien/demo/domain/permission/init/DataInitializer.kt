package io.flavien.demo.domain.permission.init

import io.flavien.demo.domain.permission.entity.Permission
import io.flavien.demo.domain.permission.model.PermissionEnum
import io.flavien.demo.domain.permission.repository.PermissionRepository
import io.flavien.demo.domain.tenant.TenantContext
import io.flavien.demo.domain.tenant.repository.TenantRegistry
import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class DataInitializer(
    private val permissionRepository: PermissionRepository,
    private val tenantRegistry: TenantRegistry,
) {
    @EventListener(ApplicationReadyEvent::class)
    fun initialize() {
        tenantRegistry.getAll().keys.forEach { tenantId ->
            try {
                TenantContext.set(tenantId)
                ensurePermissionsExist()
            } finally {
                TenantContext.clear()
            }
        }
    }

    private fun ensurePermissionsExist() {
        PermissionEnum.entries.forEach { permission ->
            if (!permissionRepository.existsById(permission)) {
                permissionRepository.save(Permission(permission))
                log.info("Created permission: ${permission.name}")
            }
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(DataInitializer::class.java)
    }
}
