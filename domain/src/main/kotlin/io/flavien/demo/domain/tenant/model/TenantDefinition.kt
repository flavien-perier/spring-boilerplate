package io.flavien.demo.domain.tenant.model

data class TenantDefinition(
    val tenantId: String,
    val db: DbDefinition,
    val redis: RedisDefinition,
    val smtp: SmtpDefinition,
)
