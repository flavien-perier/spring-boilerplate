package io.flavien.demo.batch.step.warnInactiveUsers

import io.flavien.demo.domain.user.entity.User
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.Step
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class WarnInactiveUsersStepConfig(
    private val jobRepository: JobRepository,
    private val warnInactiveUsersItemReader: WarnInactiveUsersItemReader,
    private val warnInactiveUsersItemProcessor: WarnInactiveUsersItemProcessor,
    private val warnInactiveUsersItemWriter: WarnInactiveUsersItemWriter,
) {
    @Bean
    fun warnInactiveUsersStep(): Step =
        StepBuilder("warnInactiveUsersStep", jobRepository)
            .chunk<User, User>(CHUNK_SIZE)
            .reader(warnInactiveUsersItemReader)
            .processor(warnInactiveUsersItemProcessor)
            .writer(warnInactiveUsersItemWriter)
            .build()

    companion object {
        private const val CHUNK_SIZE = 10
    }
}
