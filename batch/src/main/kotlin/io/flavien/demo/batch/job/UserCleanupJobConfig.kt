package io.flavien.demo.batch.job

import org.springframework.batch.core.job.Job
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.job.parameters.RunIdIncrementer
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.Step
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class UserCleanupJobConfig(
    private val jobRepository: JobRepository,
    @param:Qualifier("warnInactiveUsersStep") private val warnInactiveUsersStep: Step,
    @param:Qualifier("deleteInactiveUsersStep") private val deleteInactiveUsersStep: Step,
) {
    @Bean
    fun userCleanupJob(): Job =
        JobBuilder("userCleanupJob", jobRepository)
            .incrementer(RunIdIncrementer())
            .start(warnInactiveUsersStep)
            .next(deleteInactiveUsersStep)
            .build()
}
