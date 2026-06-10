package io.flavien.demo.domain.tenant.configuration

import io.flavien.demo.domain.tenant.TenantContext
import org.hibernate.context.spi.CurrentTenantIdentifierResolver

class TenantContextIdentifierResolver : CurrentTenantIdentifierResolver<String> {
    override fun resolveCurrentTenantIdentifier(): String = TenantContext.require()

    override fun validateExistingCurrentSessions(): Boolean = false
}
