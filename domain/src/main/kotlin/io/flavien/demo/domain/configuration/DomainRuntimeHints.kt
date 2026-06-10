package io.flavien.demo.domain.configuration

import io.flavien.demo.domain.tenant.model.DbDefinition
import io.flavien.demo.domain.tenant.model.RedisDefinition
import io.flavien.demo.domain.tenant.model.SmtpDefinition
import io.flavien.demo.domain.tenant.model.TenantDefinition
import io.flavien.demo.domain.user.entity.User
import org.springframework.aot.hint.MemberCategory
import org.springframework.aot.hint.RuntimeHints
import org.springframework.aot.hint.RuntimeHintsRegistrar

class DomainRuntimeHints : RuntimeHintsRegistrar {
    override fun registerHints(
        hints: RuntimeHints,
        classLoader: ClassLoader?,
    ) {
        hints.resources().registerPattern("domain*.properties")
        hints.resources().registerPattern("db/changelog/*")
        hints.resources().registerPattern("templates/*")

        listOf(
            TenantDefinition::class.java,
            DbDefinition::class.java,
            RedisDefinition::class.java,
            SmtpDefinition::class.java,
            User::class.java,
        ).forEach {
            hints.reflection().registerType(
                it,
                MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS,
                MemberCategory.DECLARED_FIELDS,
                MemberCategory.INVOKE_PUBLIC_METHODS,
            )
        }
    }
}
