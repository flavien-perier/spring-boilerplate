package io.flavien.demo.batch.step.warnInactiveUsers

import io.flavien.demo.domain.user.entity.User
import io.flavien.demo.domain.user.repository.UserRepository
import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.batch.infrastructure.item.ItemReader
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Component
import java.time.OffsetDateTime

@Component
@StepScope
class WarnInactiveUsersItemReader(
    private val userRepository: UserRepository,
    @param:Value("\${app.batch.user-cleanup.warn-threshold-months}") private val warnThresholdMonths: Long,
) : ItemReader<User> {
    private val inactiveSince: OffsetDateTime = OffsetDateTime.now().minusMonths(warnThresholdMonths)
    private val buffer: MutableList<User> = mutableListOf()
    private var initialized: Boolean = false

    override fun read(): User? {
        if (!initialized) {
            initialized = true
            var pageNumber = 0
            do {
                val page = userRepository.findUsersToWarn(inactiveSince, PageRequest.of(pageNumber++, PAGE_SIZE))
                buffer.addAll(page.content)
                if (page.isLast) break
            } while (true)
        }
        return if (buffer.isEmpty()) null else buffer.removeFirst()
    }

    companion object {
        private const val PAGE_SIZE = 10
    }
}
