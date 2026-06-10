package io.flavien.demo.batch.runner

import io.flavien.demo.domain.tenant.TenantContext
import io.flavien.demo.domain.tenant.repository.TenantRegistry
import org.slf4j.LoggerFactory
import org.springframework.batch.core.BatchStatus
import org.springframework.batch.core.job.Job
import org.springframework.batch.core.job.parameters.JobParametersBuilder
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component

@Component
class TenantJobRunner(
    private val tenantRegistry: TenantRegistry,
    private val jobLauncher: JobLauncher,
    @param:Qualifier("userCleanupJob") private val userCleanupJob: Job,
) : ApplicationRunner {
    override fun run(args: ApplicationArguments) {
        val failedTenants = mutableListOf<String>()

        tenantRegistry.getAll().keys.sorted().forEach { tenantId ->
            try {
                TenantContext.set(tenantId)
                log.info("Running userCleanupJob for tenant $tenantId")
                val jobParameters =
                    JobParametersBuilder()
                        .addString("tenantId", tenantId)
                        .addLong("timestamp", System.currentTimeMillis())
                        .toJobParameters()
                val execution = jobLauncher.run(userCleanupJob, jobParameters)
                if (execution.status != BatchStatus.COMPLETED) {
                    failedTenants.add(tenantId)
                    val failures = execution.allFailureExceptions
                    if (failures.isNotEmpty()) {
                        log.error(
                            "userCleanupJob failed for tenant $tenantId with status ${execution.status}",
                            failures.first(),
                        )
                    } else {
                        log.error("userCleanupJob failed for tenant $tenantId with status ${execution.status}")
                    }
                } else {
                    log.info("Completed userCleanupJob for tenant $tenantId")
                }
            } catch (exception: Exception) {
                failedTenants.add(tenantId)
                log.error("userCleanupJob failed for tenant $tenantId", exception)
            } finally {
                TenantContext.clear()
            }
        }

        if (failedTenants.isNotEmpty()) {
            throw IllegalStateException("userCleanupJob failed for tenant(s): ${failedTenants.joinToString(", ")}")
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(TenantJobRunner::class.java)
    }
}
