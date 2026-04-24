package io.flavien.demo.api.e2e

import io.flavien.demo.api.generated.dto.LoginDto
import io.flavien.demo.api.generated.dto.SessionRenewalDto
import io.flavien.demo.api.generated.dto.UserCreationDto
import io.flavien.demo.api.testCore.util.MailServerUtil
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
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
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SessionManagementE2eTest {
    @LocalServerPort
    private var port: Int = 0

    private val webTestClient: WebTestClient by lazy {
        WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()
    }

    @Test
    @Order(1)
    fun `Creating a first user`() {
        val requestContent = UserCreationDto(userEmail1, userPassword1, userProofOfWork1)
        webTestClient
            .post()
            .uri("/api/user")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .body(Mono.just(requestContent), UserCreationDto::class.java)
            .exchange()
            .expectStatus()
            .isNoContent
    }

    @Test
    @Order(2)
    fun `Validate the creation of the first user thanks to the code received by email`() {
        val lastMail = MailServerUtil.getLastMail(smtp, 1)
        val mailContent = String(lastMail.data)

        val activationToken = "href=.*activationToken=([^\"]+)".toRegex().find(mailContent)!!.groups[1]!!.value

        assertThat(lastMail.envelopeReceiver).isEqualTo(userEmail1)
        webTestClient
            .post()
            .uri {
                it
                    .path("/api/user/activate")
                    .queryParam("token", activationToken)
                    .build()
            }.contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent
    }

    @Test
    @Order(3)
    fun `Creating a second user`() {
        val requestContent = UserCreationDto(userEmail2, userPassword2, userProofOfWork2)
        webTestClient
            .post()
            .uri("/api/user")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .body(Mono.just(requestContent), UserCreationDto::class.java)
            .exchange()
            .expectStatus()
            .isNoContent
    }

    @Test
    @Order(4)
    fun `Validate the creation of the second user thanks to the code received by email`() {
        val lastMail = MailServerUtil.getLastMail(smtp, 2)
        val mailContent = String(lastMail.data)

        val activationToken = "href=.*activationToken=([^\"]+)".toRegex().find(mailContent)!!.groups[1]!!.value

        assertThat(lastMail.envelopeReceiver).isEqualTo(userEmail2)
        webTestClient
            .post()
            .uri {
                it
                    .path("/api/user/activate")
                    .queryParam("token", activationToken)
                    .build()
            }.contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent
    }

    @Test
    @Order(5)
    fun `Authentication failed with wrong email (For the first user)`() {
        val requestContent = LoginDto(badEmail, userPassword1, userProofOfWork1)
        webTestClient
            .post()
            .uri("/api/session/login")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .body(Mono.just(requestContent), LoginDto::class.java)
            .exchange()
            .expectStatus()
            .is4xxClientError()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.status")
            .isNotEmpty()
            .jsonPath("$.status")
            .isEqualTo("404")
    }

    @Test
    @Order(6)
    fun `Authentication failed with wrong password (For the first user)`() {
        val requestContent = LoginDto(userEmail1, badPassword, userProofOfWork1)
        webTestClient
            .post()
            .uri("/api/session/login")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .body(Mono.just(requestContent), LoginDto::class.java)
            .exchange()
            .expectStatus()
            .is4xxClientError()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.status")
            .isNotEmpty()
            .jsonPath("$.status")
            .isEqualTo("401")
    }

    @Test
    @Order(7)
    fun `Authentication failed with wrong proof of work (For the first user)`() {
        val requestContent = LoginDto(userEmail1, userPassword1, badProofOfWork1)
        webTestClient
            .post()
            .uri("/api/session/login")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .body(Mono.just(requestContent), LoginDto::class.java)
            .exchange()
            .expectStatus()
            .is4xxClientError()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.status")
            .isNotEmpty()
            .jsonPath("$.status")
            .isEqualTo("401")
    }

    @Test
    @Order(8)
    fun `First authentication with the first user`() {
        val requestContent = LoginDto(userEmail1, userPassword1, userProofOfWork1)
        webTestClient
            .post()
            .uri("/api/session/login")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .body(Mono.just(requestContent), LoginDto::class.java)
            .exchange()
            .expectStatus()
            .isOk
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.refreshToken")
            .isNotEmpty()
            .jsonPath("$.refreshToken")
            .value<String> { refreshTokenSessionUser11 = it }
            .jsonPath("$.accessToken")
            .isNotEmpty()
            .jsonPath("$.accessToken")
            .value<String> { accessTokenSessionUser11 = it }
    }

    @Test
    @Order(9)
    fun `Second authentication with the first user`() {
        val requestContent = LoginDto(userEmail1, userPassword1, userProofOfWork1)
        webTestClient
            .post()
            .uri("/api/session/login")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .body(Mono.just(requestContent), LoginDto::class.java)
            .exchange()
            .expectStatus()
            .isOk
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.refreshToken")
            .isNotEmpty()
            .jsonPath("$.refreshToken")
            .value<String> { refreshTokenSessionUser12 = it }
            .jsonPath("$.accessToken")
            .isNotEmpty()
            .jsonPath("$.accessToken")
            .value<String> { accessTokenSessionUser12 = it }
    }

    @Test
    @Order(10)
    fun `First authentication with the second user`() {
        val requestContent = LoginDto(userEmail2, userPassword2, userProofOfWork2)
        webTestClient
            .post()
            .uri("/api/session/login")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .body(Mono.just(requestContent), LoginDto::class.java)
            .exchange()
            .expectStatus()
            .isOk
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.refreshToken")
            .isNotEmpty()
            .jsonPath("$.refreshToken")
            .value<String> { refreshTokenSessionUser21 = it }
            .jsonPath("$.accessToken")
            .isNotEmpty()
            .jsonPath("$.accessToken")
            .value<String> { accessTokenSessionUser21 = it }
    }

    @Test
    @Order(11)
    fun `The first user displays his sessions`() {
        webTestClient
            .get()
            .uri("/api/session")
            .accept(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer $accessTokenSessionUser11")
            .exchange()
            .expectStatus()
            .isOk
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$")
            .isArray()
            .jsonPath("$.length()")
            .isEqualTo(2)
    }

    @Test
    @Order(12)
    fun `Using refresh token for the first user`() {
        val requestRenew = SessionRenewalDto(userEmail1, refreshTokenSessionUser11!!)

        webTestClient
            .post()
            .uri("/api/session/renew")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .body(Mono.just(requestRenew), SessionRenewalDto::class.java)
            .exchange()
            .expectStatus()
            .isOk
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.refreshToken")
            .isEmpty()
            .jsonPath("$.accessToken")
            .isNotEmpty()
            .jsonPath("$.accessToken")
            .value<String> { accessTokenSessionUser21 = it }
    }

    companion object {
        private var refreshTokenSessionUser11: String? = null
        private var accessTokenSessionUser11: String? = null
        private var refreshTokenSessionUser11Uuid: String? = null

        private var refreshTokenSessionUser12: String? = null
        private var accessTokenSessionUser12: String? = null
        private var refreshTokenSessionUser12Uuid: String? = null

        private var refreshTokenSessionUser21: String? = null
        private var accessTokenSessionUser21: String? = null
        private var refreshTokenSessionUser21Uuid: String? = null

        private val userEmail1 = "test1@flavien.cc"
        private val userPassword1 = "Password123!"
        private val userProofOfWork1 = "98361fa40e1c419c38794383924e99758ab786b2f7cf3dd5898e95167d088257"

        private val userEmail2 = "test2@flavien.cc"
        private val userPassword2 = "Password123!"
        private val userProofOfWork2 = "a1b2c3d4e5f6a7b8c9d0e1f2a3b4c5d6e7f8a9b0c1d2e3f4a5b6c7d8e9f0a1b2"

        private val badEmail = "no@flavien.cc"
        private val badPassword = "BadPassword123!"
        private val badProofOfWork1 = "0000000000000000000000000000000000000000000000000000000000000000"

        private val valkeyPassword = "password"

        @JvmStatic
        @DynamicPropertySource
        fun registerProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.data.redis.host", valkeyContainer::getHost)
            registry.add("spring.data.redis.port") {
                valkeyContainer.getMappedPort(6379).toString()
            }
            registry.add("spring.data.redis.password") {
                valkeyPassword
            }
            registry.add("spring.mail.port") {
                smtp.server.port
            }
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
                .withCommand("valkey-server --requirepass $valkeyPassword")
                .withExposedPorts(6379)
    }
}
