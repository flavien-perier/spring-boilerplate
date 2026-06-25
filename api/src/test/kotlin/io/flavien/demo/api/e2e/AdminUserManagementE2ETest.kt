package io.flavien.demo.api.e2e

import io.flavien.demo.api.generated.dto.LoginDto
import io.flavien.demo.api.generated.dto.UserCreationDto
import io.flavien.demo.api.generated.dto.UserUpdateAdminDto
import io.flavien.demo.api.testCore.util.MailServerUtil
import io.flavien.demo.domain.tenant.TenantContext
import io.flavien.demo.domain.user.model.UserUpdate
import io.flavien.demo.domain.user.service.UserService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.MediaType
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.reactive.server.WebTestClient
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName
import reactor.core.publisher.Mono
import java.io.File
import java.nio.file.Files

@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AdminUserManagementE2ETest {
    @LocalServerPort
    private var port: Int = 0

    @Autowired
    private lateinit var userService: UserService

    private val webTestClient: WebTestClient by lazy {
        WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()
    }

    @Test
    @Order(0)
    fun `Seed CSRF token`() {
        val result =
            webTestClient
                .get()
                .uri("/api/conf")
                .header("X-Tenant-Id", "test-tenant")
                .exchange()
                .returnResult(Void::class.java)
        csrfToken = result.responseCookies.getFirst("XSRF-TOKEN")?.value
            ?: throw IllegalStateException("XSRF-TOKEN cookie not found")
    }

    @Test
    @Order(1)
    fun `Seed known admin credentials`() {
        TenantContext.set("test-tenant")
        try {
            userService.updateByEmail(
                ADMIN_EMAIL,
                UserUpdate(password = ADMIN_PASSWORD, proofOfWork = ADMIN_PROOF_OF_WORK),
            )
        } finally {
            TenantContext.clear()
        }
    }

    @Test
    @Order(2)
    fun `Create a target user`() {
        val requestContent = UserCreationDto(TARGET_EMAIL, TARGET_PASSWORD, TARGET_PROOF_OF_WORK)
        webTestClient
            .post()
            .uri("/api/users")
            .header("X-Tenant-Id", "test-tenant")
            .contentType(MediaType.APPLICATION_JSON)
            .cookie("XSRF-TOKEN", csrfToken!!)
            .header("X-XSRF-TOKEN", csrfToken!!)
            .body(Mono.just(requestContent), UserCreationDto::class.java)
            .exchange()
            .expectStatus()
            .isNoContent
    }

    @Test
    @Order(3)
    fun `Activate the target user`() {
        val lastMail = MailServerUtil.getLastMail(smtp, 1)
        val mailContent = String(lastMail.data)
        val activationToken = "href=.*activationToken=([^\"]+)".toRegex().find(mailContent)!!.groups[1]!!.value

        assertThat(lastMail.envelopeReceiver).isEqualTo(TARGET_EMAIL)
        webTestClient
            .post()
            .uri {
                it
                    .path("/api/users/activate")
                    .queryParam("token", activationToken)
                    .build()
            }.header("X-Tenant-Id", "test-tenant")
            .contentType(MediaType.APPLICATION_JSON)
            .cookie("XSRF-TOKEN", csrfToken!!)
            .header("X-XSRF-TOKEN", csrfToken!!)
            .exchange()
            .expectStatus()
            .isNoContent
    }

    @Test
    @Order(4)
    fun `Authenticate as admin`() {
        val requestContent = LoginDto(ADMIN_EMAIL, ADMIN_PASSWORD, ADMIN_PROOF_OF_WORK)
        webTestClient
            .post()
            .uri("/api/session/login")
            .header("X-Tenant-Id", "test-tenant")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .cookie("XSRF-TOKEN", csrfToken!!)
            .header("X-XSRF-TOKEN", csrfToken!!)
            .body(Mono.just(requestContent), LoginDto::class.java)
            .exchange()
            .expectStatus()
            .isOk
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.accessToken")
            .isNotEmpty()
            .jsonPath("$.accessToken")
            .value<String> { adminAccessToken = it }
    }

    @Test
    @Order(5)
    fun `Admin disables the target user`() {
        val requestContent = UserUpdateAdminDto(enabled = false)
        webTestClient
            .put()
            .uri("/api/users/$TARGET_EMAIL")
            .header("X-Tenant-Id", "test-tenant")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer $adminAccessToken")
            .body(Mono.just(requestContent), UserUpdateAdminDto::class.java)
            .exchange()
            .expectStatus()
            .isOk
            .expectBody()
            .jsonPath("$.enabled")
            .isEqualTo(false)
    }

    @Test
    @Order(6)
    fun `Admin re-enables the target user`() {
        val requestContent = UserUpdateAdminDto(enabled = true)
        webTestClient
            .put()
            .uri("/api/users/$TARGET_EMAIL")
            .header("X-Tenant-Id", "test-tenant")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer $adminAccessToken")
            .body(Mono.just(requestContent), UserUpdateAdminDto::class.java)
            .exchange()
            .expectStatus()
            .isOk
            .expectBody()
            .jsonPath("$.enabled")
            .isEqualTo(true)
    }

    @Test
    @Order(7)
    fun `Admin resets the target user OTP`() {
        webTestClient
            .delete()
            .uri("/api/users/$TARGET_EMAIL/otp")
            .header("X-Tenant-Id", "test-tenant")
            .header("Authorization", "Bearer $adminAccessToken")
            .exchange()
            .expectStatus()
            .isNoContent
    }

    @Test
    @Order(8)
    fun `Admin triggers a password reset for the target user`() {
        webTestClient
            .post()
            .uri("/api/users/$TARGET_EMAIL/reset-password")
            .header("X-Tenant-Id", "test-tenant")
            .header("Authorization", "Bearer $adminAccessToken")
            .exchange()
            .expectStatus()
            .isNoContent

        val lastMail = MailServerUtil.getLastMail(smtp, 2)
        assertThat(lastMail.envelopeReceiver).isEqualTo(TARGET_EMAIL)
    }

    companion object {
        private var csrfToken: String? = null
        private var adminAccessToken: String? = null

        private const val ADMIN_EMAIL = "perier@flavien.io"
        private const val ADMIN_PASSWORD = "Password123!"
        private const val ADMIN_PROOF_OF_WORK = "98361fa40e1c419c38794383924e99758ab786b2f7cf3dd5898e95167d088257"

        private const val TARGET_EMAIL = "target@flavien.cc"
        private const val TARGET_PASSWORD = "Password123!"
        private const val TARGET_PROOF_OF_WORK = "a1b2c3d4e5f6a7b8c9d0e1f2a3b4c5d6e7f8a9b0c1d2e3f4a5b6c7d8e9f0a1b2"

        private val valkeyPassword = "password"

        val smtp = MailServerUtil.create()

        @Container
        val postgresContainer = PostgreSQLContainer("postgres:18-alpine")

        @Container
        val valkeyContainer =
            GenericContainer(DockerImageName.parse("valkey/valkey:7-alpine"))
                .withCommand("valkey-server --requirepass $valkeyPassword")
                .withExposedPorts(6379)

        @JvmStatic
        @DynamicPropertySource
        fun registerProperties(registry: DynamicPropertyRegistry) {
            val tenantDir = Files.createTempDirectory("e2e-tenants").toFile()
            val tenantYaml =
                """
                tenantId: test-tenant
                db:
                  jdbcUrl: ${postgresContainer.jdbcUrl}
                  username: ${postgresContainer.username}
                  password: ${postgresContainer.password}
                  schema: public
                redis:
                  host: ${valkeyContainer.host}
                  port: ${valkeyContainer.getMappedPort(6379)}
                  password: $valkeyPassword
                  database: 0
                smtp:
                  host: localhost
                  port: ${smtp.server.port}
                  username: ""
                  password: ""
                  auth: false
                  starttls: false
                  accountCreator: no-reply@test.io
                  domainLinks: http://localhost
                """.trimIndent()
            File(tenantDir, "test-tenant.yml").writeText(tenantYaml)
            registry.add("flavien-io.tenants.directory") { tenantDir.absolutePath }
        }

        @JvmStatic
        @AfterAll
        fun stopSmtp() {
            smtp.stop()
        }
    }
}
