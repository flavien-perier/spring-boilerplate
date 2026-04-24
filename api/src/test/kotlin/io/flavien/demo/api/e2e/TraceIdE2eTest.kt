package io.flavien.demo.api.e2e

import io.flavien.demo.api.generated.dto.LoginDto
import io.flavien.demo.api.testCore.util.MailServerUtil
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
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

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TraceIdE2eTest {
    @LocalServerPort
    private var port: Int = 0

    private val webTestClient: WebTestClient by lazy {
        WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()
    }

    @Test
    fun `Every API response includes a X-Trace-Id header with a 32-char hex value`() {
        val requestContent = LoginDto("nonexistent@test.com", "Password123!", "pow")
        webTestClient
            .post()
            .uri("/api/session/login")
            .contentType(MediaType.APPLICATION_JSON)
            .body(Mono.just(requestContent), LoginDto::class.java)
            .exchange()
            .expectHeader()
            .exists("X-Trace-Id")
            .expectHeader()
            .value("X-Trace-Id") { traceId ->
                assertThat(traceId).matches("[0-9a-f]{32}")
            }
    }

    companion object {
        private val VALKEY_PASSWORD = "password"

        @JvmStatic
        @DynamicPropertySource
        fun registerProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.data.redis.host", valkeyContainer::getHost)
            registry.add("spring.data.redis.port") {
                valkeyContainer.getMappedPort(6379).toString()
            }
            registry.add("spring.data.redis.password") { VALKEY_PASSWORD }
            registry.add("spring.mail.port") { smtp.server.port }
        }

        @JvmStatic
        @AfterAll
        fun stopSmtp() {
            smtp.stop()
        }

        val smtp = MailServerUtil.create()

        @Container
        @ServiceConnection
        val postgresContainer = PostgreSQLContainer("postgres:15-alpine")

        @Container
        val valkeyContainer =
            GenericContainer(DockerImageName.parse("valkey/valkey:7-alpine"))
                .withCommand("valkey-server --requirepass $VALKEY_PASSWORD")
                .withExposedPorts(6379)
    }
}
