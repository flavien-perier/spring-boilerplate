package io.flavien.demo.batch.step.warnInactiveUsers

import io.flavien.demo.domain.user.entity.User
import org.springframework.batch.infrastructure.item.ItemProcessor
import org.springframework.stereotype.Component

@Component
class WarnInactiveUsersItemProcessor : ItemProcessor<User, User> {
    override fun process(user: User): User? = if (user.deletionWarningSentAt == null) user else null
}
