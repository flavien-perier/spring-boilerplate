package io.flavien.demo.domain.tenant

import io.flavien.demo.domain.tenant.exception.TenantNotFoundException

object TenantContext {
    private val current = ThreadLocal<String?>()

    fun set(tenantId: String) {
        current.set(tenantId)
    }

    fun get(): String? = current.get()

    fun require(): String = get() ?: throw TenantNotFoundException("unset")

    fun clear() {
        current.remove()
    }
}
