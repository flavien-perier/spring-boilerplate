package io.flavien.demo.batch

import io.flavien.demo.domain.group.repository.UserGroupRepository
import io.flavien.demo.domain.permission.repository.UserPermissionRepository
import io.flavien.demo.domain.tenant.TenantContext
import io.flavien.demo.domain.user.repository.UserRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.batch.core.BatchStatus
import org.springframework.batch.core.job.Job
import org.springframework.batch.core.job.parameters.JobParametersBuilder
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName
import java.io.File
import java.nio.file.Files
import java.time.OffsetDateTime

@SpringBootTest
@Testcontainers
class UserCleanupJobIntegrationTest {
    companion object {
        @Container
        @JvmField
        val postgres: PostgreSQLContainer<Nothing> = PostgreSQLContainer("postgres:18-alpine")

        @Container
        @JvmField
        val postgresBatch: PostgreSQLContainer<Nothing> = PostgreSQLContainer("postgres:18-alpine")

        @Container
        @JvmField
        val valkey: GenericContainer<*> =
            GenericContainer<Nothing>(DockerImageName.parse("valkey/valkey:7-alpine"))
                .withExposedPorts(6379)

        @JvmStatic
        @DynamicPropertySource
        fun configureTestProperties(registry: DynamicPropertyRegistry) {
            val tenantDir = Files.createTempDirectory("batch-tenants").toFile()
            val tenantYaml =
                """
                tenantId: test-tenant
                db:
                  jdbcUrl: ${postgres.jdbcUrl}
                  username: ${postgres.username}
                  password: ${postgres.password}
                  schema: public
                redis:
                  host: ${valkey.host}
                  port: ${valkey.getMappedPort(6379)}
                smtp:
                  host: localhost
                  port: 25
                  username: ""
                  password: ""
                  auth: false
                  starttls: false
                  accountCreator: no-reply@test.io
                  domainLinks: http://localhost
                """.trimIndent()
            File(tenantDir, "test-tenant.yml").writeText(tenantYaml)
            registry.add("flavien-io.tenants.directory") { tenantDir.absolutePath }
            registry.add("spring.batch.datasource.url") { postgresBatch.jdbcUrl }
            registry.add("spring.batch.datasource.username") { postgresBatch.username }
            registry.add("spring.batch.datasource.password") { postgresBatch.password }
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

    @Autowired
    lateinit var userGroupRepository: UserGroupRepository

    @Autowired
    lateinit var userPermissionRepository: UserPermissionRepository

    @BeforeEach
    fun setupTenant() {
        TenantContext.set("test-tenant")
    }

    @AfterEach
    fun cleanup() {
        userPermissionRepository.deleteAll()
        userGroupRepository.deleteAll()
        userRepository.deleteAll()
        TenantContext.clear()
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
