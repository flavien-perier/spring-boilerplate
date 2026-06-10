package io.flavien.demo.api.configuration.filter

import io.flavien.demo.domain.tenant.TenantContext
import io.flavien.demo.domain.tenant.exception.TenantNotFoundException
import io.flavien.demo.domain.tenant.model.DbDefinition
import io.flavien.demo.domain.tenant.model.RedisDefinition
import io.flavien.demo.domain.tenant.model.SmtpDefinition
import io.flavien.demo.domain.tenant.model.TenantDefinition
import io.flavien.demo.domain.tenant.repository.TenantRegistry
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse

private class ExposedTenantResolutionFilter(
    registry: TenantRegistry,
) : TenantResolutionFilter(registry) {
    public override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) = super.doFilterInternal(request, response, filterChain)

    public override fun shouldNotFilter(request: HttpServletRequest): Boolean = super.shouldNotFilter(request)
}

class TenantResolutionFilterTest {
    private val tenantRegistry: TenantRegistry = mockk()
    private val filter = ExposedTenantResolutionFilter(tenantRegistry)

    @AfterEach
    fun cleanup() {
        TenantContext.clear()
    }

    private fun validTenant(id: String) =
        TenantDefinition(
            tenantId = id,
            db =
                DbDefinition(
                    jdbcUrl = "jdbc:postgresql://localhost/test",
                    username = "user",
                    password = "pass",
                    schema = "schema_$id",
                ),
            redis = RedisDefinition(host = "localhost"),
            smtp =
                SmtpDefinition(
                    host = "localhost",
                    accountCreator = "no-reply@test.io",
                    domainLinks = "http://localhost",
                ),
        )

    @Test
    fun `valid header — TenantContext is set during doFilter, cleared after`() {
        val request = MockHttpServletRequest().apply { addHeader("X-Tenant-Id", "acme") }
        val response = MockHttpServletResponse()
        val chain: FilterChain = mockk(relaxed = true)

        every { tenantRegistry.get("acme") } returns validTenant("acme")

        var contextDuringFilter: String? = null
        every { chain.doFilter(any(), any()) } answers {
            contextDuringFilter = TenantContext.get()
        }

        filter.doFilterInternal(request, response, chain)

        assertThat(contextDuringFilter).isEqualTo("acme")
        assertThat(TenantContext.get()).isNull()
        verify { chain.doFilter(request, response) }
    }

    @Test
    fun `missing header defaults to app tenant`() {
        val request = MockHttpServletRequest()
        val response = MockHttpServletResponse()
        val chain: FilterChain = mockk(relaxed = true)

        every { tenantRegistry.get("app") } returns validTenant("app")

        var contextDuringFilter: String? = null
        every { chain.doFilter(any(), any()) } answers {
            contextDuringFilter = TenantContext.get()
        }

        filter.doFilterInternal(request, response, chain)

        assertThat(contextDuringFilter).isEqualTo("app")
        assertThat(response.status).isEqualTo(HttpServletResponse.SC_OK)
        verify { chain.doFilter(request, response) }
    }

    @Test
    fun `blank header defaults to app tenant`() {
        val request = MockHttpServletRequest().apply { addHeader("X-Tenant-Id", "   ") }
        val response = MockHttpServletResponse()
        val chain: FilterChain = mockk(relaxed = true)

        every { tenantRegistry.get("app") } returns validTenant("app")

        var contextDuringFilter: String? = null
        every { chain.doFilter(any(), any()) } answers {
            contextDuringFilter = TenantContext.get()
        }

        filter.doFilterInternal(request, response, chain)

        assertThat(contextDuringFilter).isEqualTo("app")
        assertThat(response.status).isEqualTo(HttpServletResponse.SC_OK)
        verify { chain.doFilter(request, response) }
    }

    @Test
    fun `unknown tenantId causes TenantNotFoundException to propagate`() {
        val request = MockHttpServletRequest().apply { addHeader("X-Tenant-Id", "unknown") }
        val response = MockHttpServletResponse()
        val chain: FilterChain = mockk(relaxed = true)

        every { tenantRegistry.get("unknown") } throws TenantNotFoundException("unknown")

        assertThatThrownBy { filter.doFilterInternal(request, response, chain) }
            .isInstanceOf(TenantNotFoundException::class.java)
    }

    @Test
    fun `actuator path is skipped — shouldNotFilter returns true`() {
        val request = MockHttpServletRequest().apply { requestURI = "/actuator/health" }

        val shouldSkip = filter.shouldNotFilter(request)

        assertThat(shouldSkip).isTrue()
        verify(exactly = 0) { tenantRegistry.get(any()) }
    }
}
