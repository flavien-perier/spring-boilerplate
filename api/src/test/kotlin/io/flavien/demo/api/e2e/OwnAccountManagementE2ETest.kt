package io.flavien.demo.api.e2e

import io.flavien.demo.api.dto.LoginDto
import io.flavien.demo.api.dto.UserCreationDto
import io.flavien.demo.api.dto.UserUpdateDto
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
class OwnAccountManagementE2ETest {
    @LocalServerPort
    private var port: Int = 0

    private val webTestClient: WebTestClient by lazy {
        WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()
    }

    @Test
    @Order(1)
    fun `Creating a new user`() {
        val requestContent = UserCreationDto(userEmail, userPassword, userProofOfWork)
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
    fun `Try to create an account with the same email address`() {
        val requestContent = UserCreationDto(userEmail, userPassword, userProofOfWork)
        webTestClient
            .post()
            .uri("/api/user")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .body(Mono.just(requestContent), UserCreationDto::class.java)
            .exchange()
            .expectStatus()
            .is4xxClientError()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.status")
            .isNotEmpty()
            .jsonPath("$.status")
            .isEqualTo("409")
    }

    @Test
    @Order(3)
    fun `Failed to validate the creation of the account thanks to the code received by email`() {
        webTestClient
            .post()
            .uri {
                it
                    .path("/api/user/activate")
                    .queryParam("token", "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa")
                    .build()
            }.contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isUnauthorized
    }

    @Test
    @Order(4)
    fun `Validate the creation of the account thanks to the code received by email`() {
        val lastMail = MailServerUtil.getLastMail(smtp, 1)
        val mailContent = String(lastMail.data)

        val activationToken = "href=.*activationToken=([^\"]+)".toRegex().find(mailContent)!!.groups[1]!!.value

        assertThat(lastMail.envelopeReceiver).isEqualTo(userEmail)
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
    fun `Authentication with created user`() {
        val requestContent = LoginDto(userEmail, userPassword, userProofOfWork)
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
            .value<String> { refreshToken = it }
            .jsonPath("$.accessToken")
            .isNotEmpty()
            .jsonPath("$.accessToken")
            .value<String> { accessToken = it }
    }

    @Test
    @Order(6)
    fun `The user displays his information`() {
        webTestClient
            .get()
            .uri("/api/user/me")
            .accept(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer $accessToken")
            .exchange()
            .expectStatus()
            .isOk
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.email")
            .isNotEmpty()
            .jsonPath("$.email")
            .isEqualTo(userEmail)
            .jsonPath("$.role")
            .isNotEmpty()
            .jsonPath("$.role")
            .isEqualTo("USER")
    }

    @Test
    @Order(7)
    fun `The user modifies his information`() {
        val requestContent = UserUpdateDto(userUpdatedEmail, userUpdatedPassword, userProofOfWork)
        webTestClient
            .put()
            .uri("/api/user/me")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer $accessToken")
            .body(Mono.just(requestContent), UserUpdateDto::class.java)
            .exchange()
            .expectStatus()
            .isOk
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.email")
            .isNotEmpty()
            .jsonPath("$.email")
            .isEqualTo(userUpdatedEmail)
            .jsonPath("$.role")
            .isNotEmpty()
            .jsonPath("$.role")
            .isEqualTo("USER")
    }

    @Test
    @Order(8)
    fun `Another user tries to create an account with the old email address`() {
        val requestContent = UserCreationDto(userEmail, userPassword, userProofOfWork)
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
    @Order(9)
    fun `User re-displays information after modification`() {
        webTestClient
            .get()
            .uri("/api/user/me")
            .accept(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer $accessToken")
            .exchange()
            .expectStatus()
            .isOk
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.email")
            .isNotEmpty()
            .jsonPath("$.email")
            .isEqualTo(userUpdatedEmail)
            .jsonPath("$.role")
            .isNotEmpty()
            .jsonPath("$.role")
            .isEqualTo("USER")
    }

    @Test
    @Order(10)
    fun `User deletes his account`() {
        webTestClient
            .delete()
            .uri("/api/user/me")
            .accept(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer $accessToken")
            .exchange()
            .expectStatus()
            .isNoContent
    }

    @Test
    @Order(11)
    fun `User tries to redisplay information after account deletion`() {
        webTestClient
            .get()
            .uri("/api/user/me")
            .accept(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer $accessToken")
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
    @Order(12)
    fun `User attempts to re-authenticate after account deletion`() {
        val requestContent = LoginDto(userUpdatedEmail, userUpdatedPassword, userProofOfWork)
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

    companion object {
        private var refreshToken: String? = null
        private var accessToken: String? = null

        private val userEmail = "test1@flavien.cc"
        private val userPassword = "Password123!"
        private val userProofOfWork = "98361fa40e1c419c38794383924e99758ab786b2f7cf3dd5898e95167d088257"
        private val userUpdatedEmail = "test2@flavien.cc"
        private val userUpdatedPassword = "NewPassword123!"

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
