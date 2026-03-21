package io.flavien.demo.batch.step.warnInactiveUsers

import io.flavien.demo.domain.user.entity.User
import io.flavien.demo.domain.user.service.UserDeletionWarningService
import org.slf4j.LoggerFactory
import org.springframework.batch.infrastructure.item.Chunk
import org.springframework.batch.infrastructure.item.ItemWriter
import org.springframework.stereotype.Component

@Component
class WarnInactiveUsersItemWriter(
    private val userDeletionWarningService: UserDeletionWarningService,
) : ItemWriter<User> {
    override fun write(chunk: Chunk<out User>) {
        chunk.forEach { user ->
            logger.info("Sending deletion warning to ${user.email}")
            userDeletionWarningService.sendWarningEmail(user)
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(WarnInactiveUsersItemWriter::class.java)
    }
}
