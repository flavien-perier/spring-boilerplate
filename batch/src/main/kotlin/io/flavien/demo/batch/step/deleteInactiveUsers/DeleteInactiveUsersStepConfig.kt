package io.flavien.demo.batch.step.deleteInactiveUsers

import io.flavien.demo.domain.user.entity.User
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.Step
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class DeleteInactiveUsersStepConfig(
    private val jobRepository: JobRepository,
    private val deleteInactiveUsersItemReader: DeleteInactiveUsersItemReader,
    private val deleteInactiveUsersItemWriter: DeleteInactiveUsersItemWriter,
) {
    @Bean
    fun deleteInactiveUsersStep(): Step =
        StepBuilder("deleteInactiveUsersStep", jobRepository)
            .chunk<User, User>(CHUNK_SIZE)
            .reader(deleteInactiveUsersItemReader)
            .writer(deleteInactiveUsersItemWriter)
            .build()

    companion object {
        private const val CHUNK_SIZE = 10
    }
}
