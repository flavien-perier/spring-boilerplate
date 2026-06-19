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
    private var pageNumber: Int = 0

    override fun read(): User? {
        while (buffer.isEmpty()) {
            val page = userRepository.findUsersToWarn(inactiveSince, PageRequest.of(pageNumber, PAGE_SIZE))
            if (page.content.isEmpty()) {
                return null
            }
            buffer.addAll(page.content)
            pageNumber++
            if (page.isLast) {
                break
            }
        }
        return if (buffer.isEmpty()) null else buffer.removeFirst()
    }

    companion object {
        private const val PAGE_SIZE = 10
    }
}
