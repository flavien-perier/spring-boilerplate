package io.flavien.demo.batch.step.deleteInactiveUsers

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
class DeleteInactiveUsersItemReader(
    private val userRepository: UserRepository,
    @param:Value("\${app.batch.user-cleanup.delete-threshold-months}") private val deleteThresholdMonths: Long,
) : ItemReader<User> {
    private val inactiveSince: OffsetDateTime = OffsetDateTime.now().minusMonths(deleteThresholdMonths)
    private val buffer: MutableList<User> = mutableListOf()
    private var loaded: Boolean = false

    override fun read(): User? {
        if (!loaded) {
            // Snapshot all inactive users once: the writer deletes them, so re-querying mid-chunk
            // would return users not yet removed and produce duplicate (failing) deletes.
            var pageIndex = 0
            do {
                val page = userRepository.findUsersToDelete(inactiveSince, PageRequest.of(pageIndex, PAGE_SIZE))
                buffer.addAll(page.content)
                pageIndex++
            } while (!page.content.isEmpty())
            loaded = true
        }
        return if (buffer.isEmpty()) null else buffer.removeFirst()
    }

    companion object {
        private const val PAGE_SIZE = 10
    }
}
