package io.flavien.demo.domain.tenant.exception

import io.flavien.demo.domain.shared.exception.FioException
import org.springframework.http.HttpStatus

class TenantNotFoundException(
    tenantId: String,
) : FioException("Tenant not found: $tenantId", HttpStatus.UNAUTHORIZED, "TENANT_NOT_FOUND")
