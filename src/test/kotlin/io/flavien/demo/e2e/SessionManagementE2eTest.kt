package io.flavien.demo.e2e

import io.flavien.demo.dto.LoginDto
import io.flavien.demo.dto.SessionRenewalDto
import io.flavien.demo.dto.UserCreationDto
import io.flavien.demo.testCore.util.MailServerUtil
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
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

    @Autowired
    protected lateinit var webTestClient: WebTestClient

    @Test
    @Order(1)
    fun `Creating a first user`() {
        val requestContent = UserCreationDto(userEmail1, userPassword1, userProofOfWork1)
        webTestClient.post().uri("/api/user")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .body(Mono.just(requestContent), UserCreationDto::class.java)
            .exchange()
            .expectStatus().isNoContent
    }

    @Test
    @Order(2)
    fun `Validate the creation of the first user thanks to the code received by email`() {
        val lastMail = MailServerUtil.getLastMail(smtp, 1)
        val mailContent = String(lastMail.data)

        val activationToken = "href=.*activationToken=([^\"]+)".toRegex().find(mailContent)!!.groups[1]!!.value

        assertThat(lastMail.envelopeReceiver).isEqualTo(userEmail1)
        webTestClient.post().uri {
            it.path("/api/user/activate")
                .queryParam("token", activationToken)
                .build()
        }
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isNoContent
    }

    @Test
    @Order(3)
    fun `Creating a second user`() {
        val requestContent = UserCreationDto(userEmail2, userPassword2, userProofOfWork2)
        webTestClient.post().uri("/api/user")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .body(Mono.just(requestContent), UserCreationDto::class.java)
            .exchange()
            .expectStatus().isNoContent
    }

    @Test
    @Order(4)
    fun `Validate the creation of the second user thanks to the code received by email`() {
        val lastMail = MailServerUtil.getLastMail(smtp, 2)
        val mailContent = String(lastMail.data)

        val activationToken = "href=.*activationToken=([^\"]+)".toRegex().find(mailContent)!!.groups[1]!!.value

        assertThat(lastMail.envelopeReceiver).isEqualTo(userEmail2)
        webTestClient.post().uri {
            it.path("/api/user/activate")
                .queryParam("token", activationToken)
                .build()
        }
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isNoContent
    }

    @Test
    @Order(5)
    fun `Authentication failed with wrong email (For the first user)`() {
        val requestContent = LoginDto(badEmail, userPassword1, userProofOfWork1)
        webTestClient.post().uri("/api/session/login")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .body(Mono.just(requestContent), LoginDto::class.java)
            .exchange()
            .expectStatus().is4xxClientError()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.status").isNotEmpty()
            .jsonPath("$.status").isEqualTo("404")
    }

    @Test
    @Order(6)
    fun `Authentication failed with wrong password (For the first user)`() {
        val requestContent = LoginDto(userEmail1, badPassword, userProofOfWork1)
        webTestClient.post().uri("/api/session/login")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .body(Mono.just(requestContent), LoginDto::class.java)
            .exchange()
            .expectStatus().is4xxClientError()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.status").isNotEmpty()
            .jsonPath("$.status").isEqualTo("401")
    }

    @Test
    @Order(7)
    fun `Authentication failed with wrong proof of work (For the first user)`() {
        val requestContent = LoginDto(userEmail1, userPassword1, badProofOfWork1)
        webTestClient.post().uri("/api/session/login")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .body(Mono.just(requestContent), LoginDto::class.java)
            .exchange()
            .expectStatus().is4xxClientError()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.status").isNotEmpty()
            .jsonPath("$.status").isEqualTo("401")
    }

    @Test
    @Order(8)
    fun `First authentication with the first user`() {
        val requestContent = LoginDto(userEmail1, userPassword1, userProofOfWork1)
        webTestClient.post().uri("/api/session/login")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .body(Mono.just(requestContent), LoginDto::class.java)
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.refreshToken").isNotEmpty()
            .jsonPath("$.refreshToken").value<String> { refreshTokenSessionUser1_1 = it }
            .jsonPath("$.accessToken").isNotEmpty()
            .jsonPath("$.accessToken").value<String> { accessTokenSessionUser1_1 = it }
    }

    @Test
    @Order(9)
    fun `Second authentication with the first user`() {
        val requestContent = LoginDto(userEmail1, userPassword1, userProofOfWork1)
        webTestClient.post().uri("/api/session/login")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .body(Mono.just(requestContent), LoginDto::class.java)
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.refreshToken").isNotEmpty()
            .jsonPath("$.refreshToken").value<String> { refreshTokenSessionUser1_2 = it }
            .jsonPath("$.accessToken").isNotEmpty()
            .jsonPath("$.accessToken").value<String> { accessTokenSessionUser1_2 = it }
    }

    @Test
    @Order(10)
    fun `First authentication with the second user`() {
        val requestContent = LoginDto(userEmail2, userPassword2, userProofOfWork2)
        webTestClient.post().uri("/api/session/login")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .body(Mono.just(requestContent), LoginDto::class.java)
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.refreshToken").isNotEmpty()
            .jsonPath("$.refreshToken").value<String> { refreshTokenSessionUser2_1 = it }
            .jsonPath("$.accessToken").isNotEmpty()
            .jsonPath("$.accessToken").value<String> { accessTokenSessionUser2_1 = it }
    }

    @Test
    @Order(11)
    fun `The first user displays his sessions`() {
        webTestClient.get().uri("/api/session")
            .accept(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer $accessTokenSessionUser1_1")
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$").isArray()
            .jsonPath("$.length()").isEqualTo(2)
    }

    @Test
    @Order(12)
    fun `Using refresh token for the first user`() {
        val requestRenew = SessionRenewalDto(userEmail1, refreshTokenSessionUser1_1)

        webTestClient.post().uri("/api/session/renew")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .body(Mono.just(requestRenew), SessionRenewalDto::class.java)
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.refreshToken").isEmpty()
            .jsonPath("$.accessToken").isNotEmpty()
            .jsonPath("$.accessToken").value<String> { accessTokenSessionUser2_1 = it }
    }

    companion object {
        private var refreshTokenSessionUser1_1: String? = null
        private var accessTokenSessionUser1_1: String? = null
        private var refreshTokenSessionUser1_1_uuid: String? = null

        private var refreshTokenSessionUser1_2: String? = null
        private var accessTokenSessionUser1_2: String? = null
        private var refreshTokenSessionUser1_2_uuid: String? = null

        private var refreshTokenSessionUser2_1: String? = null
        private var accessTokenSessionUser2_1: String? = null
        private var refreshTokenSessionUser2_1_uuid: String? = null

        private val userEmail1 = "test1@flavien.cc"
        private val userPassword1 = "Password123!"
        private val userProofOfWork1 = "proofOfWork"

        private val userEmail2 = "test2@flavien.cc"
        private val userPassword2 = "Password123!"
        private val userProofOfWork2 = "proofOfWork"

        private val badEmail = "no@flavien.cc"
        private val badPassword = "BadPassword123!"
        private val badProofOfWork1 = "badProofOfWork"

        private val VALKEY_PASSWORD = "password"

        @JvmStatic
        @DynamicPropertySource
        fun registerProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.data.redis.host", valkeyContainer::getHost)
            registry.add("spring.data.redis.port") {
                valkeyContainer.getMappedPort(6379).toString()
            }
            registry.add("spring.data.redis.password") {
                VALKEY_PASSWORD
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
        val valkeyContainer = GenericContainer(DockerImageName.parse("valkey/valkey:7-alpine"))
            .withCommand("valkey-server --requirepass $VALKEY_PASSWORD")
            .withExposedPorts(6379)
    }
}