package io.flavien.demo.domain.tenant

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors

class TenantContextTest {
    @AfterEach
    fun cleanup() {
        TenantContext.clear()
    }

    @Test
    fun `set and get return the same tenantId`() {
        TenantContext.set("my-tenant")
        assertThat(TenantContext.get()).isEqualTo("my-tenant")
    }

    @Test
    fun `clear makes get return null`() {
        TenantContext.set("my-tenant")
        TenantContext.clear()
        assertThat(TenantContext.get()).isNull()
    }

    @Test
    fun `two threads with different tenants do not see each other`() {
        val latch = CountDownLatch(1)
        val executor = Executors.newSingleThreadExecutor()
        var otherThreadValue: String? = "sentinel"

        TenantContext.set("main-tenant")

        val future =
            executor.submit {
                TenantContext.set("other-tenant")
                latch.countDown()
                Thread.sleep(100)
                otherThreadValue = TenantContext.get()
            }

        latch.await()
        assertThat(TenantContext.get()).isEqualTo("main-tenant")

        future.get()
        assertThat(otherThreadValue).isEqualTo("other-tenant")

        executor.shutdown()
    }
}
