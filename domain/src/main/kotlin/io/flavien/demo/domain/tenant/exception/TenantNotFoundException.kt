package io.flavien.demo.domain.tenant.exception

import io.flavien.demo.domain.shared.exception.FioException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
class TenantNotFoundException(
    tenantId: String,
) : FioException("Tenant not found: $tenantId", "TENANT_NOT_FOUND")
