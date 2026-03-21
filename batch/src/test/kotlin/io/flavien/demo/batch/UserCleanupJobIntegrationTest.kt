package io.flavien.demo.batch

import io.flavien.demo.domain.user.repository.UserRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.batch.core.BatchStatus
import org.springframework.batch.core.job.Job
import org.springframework.batch.core.job.parameters.JobParametersBuilder
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName
import java.time.OffsetDateTime

@SpringBootTest
@Testcontainers
class UserCleanupJobIntegrationTest {
    companion object {
        @Container
        @ServiceConnection
        @JvmField
        val postgres: PostgreSQLContainer<Nothing> = PostgreSQLContainer("postgres:15-alpine")

        @Container
        @JvmField
        val valkey: GenericContainer<*> =
            GenericContainer<Nothing>(DockerImageName.parse("valkey/valkey:7-alpine"))
                .withExposedPorts(6379)

        @JvmStatic
        @DynamicPropertySource
        fun configureTestProperties(registry: DynamicPropertyRegistry) {
            registry.add("VALKEY_HOST", valkey::getHost)
            registry.add("VALKEY_PORT") { valkey.getMappedPort(6379).toString() }
            registry.add("VALKEY_PASSWORD") { "" }
            registry.add("SMTP_HOST") { "localhost" }
            registry.add("SMTP_PORT") { "25" }
            registry.add("SMTP_USERNAME") { "" }
            registry.add("SMTP_PASSWORD") { "" }
            registry.add("SMTP_AUTH") { "no" }
            registry.add("SMTP_STARTTLS") { "no" }
            registry.add("MAIL_ACCOUNT_CREATOR") { "no-reply@test.io" }
            registry.add("MAIL_DOMAIN_LINKS") { "http://localhost" }
        }
    }

    @MockitoBean
    lateinit var javaMailSender: JavaMailSender

    @Autowired
    lateinit var jobLauncher: JobLauncher

    @Autowired
    lateinit var userCleanupJob: Job

    @Autowired
    lateinit var userRepository: UserRepository

    @AfterEach
    fun cleanup() {
        userRepository.deleteAll()
    }

    @Test
    fun `Should warn users inactive for 11 months and not yet warned`() {
        // Given
        val userToWarn =
            userRepository.save(
                UserBatchTestFactory.initUser(
                    email = "warn@test.io",
                    lastLogin = OffsetDateTime.now().minusMonths(11).minusWeeks(2),
                ),
            )
        userRepository.save(UserBatchTestFactory.initUser(email = "active@test.io"))

        // When
        val execution =
            jobLauncher.run(
                userCleanupJob,
                JobParametersBuilder().addLong("run.id", System.currentTimeMillis()).toJobParameters(),
            )

        // Then
        assertThat(execution.status).isEqualTo(BatchStatus.COMPLETED)
        val warned = userRepository.getUserById(userToWarn.id!!).get()
        assertThat(warned.deletionWarningSentAt).isNotNull()
        assertThat(userRepository.existsByEmail("active@test.io")).isTrue()
    }

    @Test
    fun `Should delete users inactive for 12 months`() {
        // Given
        val userToDelete =
            userRepository.save(
                UserBatchTestFactory.initUser(
                    email = "delete@test.io",
                    lastLogin = OffsetDateTime.now().minusMonths(13),
                ),
            )
        userRepository.save(UserBatchTestFactory.initUser(email = "active@test.io"))

        // When
        val execution =
            jobLauncher.run(
                userCleanupJob,
                JobParametersBuilder().addLong("run.id", System.currentTimeMillis()).toJobParameters(),
            )

        // Then
        assertThat(execution.status).isEqualTo(BatchStatus.COMPLETED)
        assertThat(userRepository.existsById(userToDelete.id!!)).isFalse()
        assertThat(userRepository.existsByEmail("active@test.io")).isTrue()
    }
}
