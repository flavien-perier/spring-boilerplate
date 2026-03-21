package io.flavien.demo.batch.step.deleteInactiveUsers

import io.flavien.demo.domain.user.entity.User
import io.flavien.demo.domain.user.service.UserService
import org.slf4j.LoggerFactory
import org.springframework.batch.infrastructure.item.Chunk
import org.springframework.batch.infrastructure.item.ItemWriter
import org.springframework.stereotype.Component

@Component
class DeleteInactiveUsersItemWriter(
    private val userService: UserService,
) : ItemWriter<User> {
    override fun write(chunk: Chunk<out User>) {
        chunk.forEach { user ->
            logger.info("Deleting inactive user ${user.email} (last login: ${user.lastLogin})")
            userService.delete(user.id!!)
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(DeleteInactiveUsersItemWriter::class.java)
    }
}
