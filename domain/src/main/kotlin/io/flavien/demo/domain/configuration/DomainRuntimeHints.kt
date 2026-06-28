package io.flavien.demo.domain.configuration

import io.flavien.demo.domain.permission.entity.Permission
import io.flavien.demo.domain.permission.entity.RolePermission
import io.flavien.demo.domain.permission.entity.UserPermission
import io.flavien.demo.domain.permission.model.PermissionEnum
import io.flavien.demo.domain.permission.model.id.RolePermissionId
import io.flavien.demo.domain.permission.model.id.UserPermissionId
import io.flavien.demo.domain.role.entity.Role
import io.flavien.demo.domain.role.entity.UserRole
import io.flavien.demo.domain.role.model.id.UserRoleId
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
            Role::class.java,
            UserRole::class.java,
            Permission::class.java,
            UserPermission::class.java,
            RolePermission::class.java,
            UserRoleId::class.java,
            UserPermissionId::class.java,
            RolePermissionId::class.java,
            PermissionEnum::class.java,
        ).forEach {
            hints.reflection().registerType(
                it,
                MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS,
                MemberCategory.DECLARED_FIELDS,
                MemberCategory.INVOKE_PUBLIC_METHODS,
            )
        }

        // Hibernate 7 instantiates this generator reflectively when building the entity
        // metamodel: both @CreationTimestamp and @UpdateTimestamp are backed by the single
        // CurrentTimestampGeneration class. It is not covered by Spring Boot's Hibernate
        // native-image hints, so it must be registered explicitly.
        hints.reflection().registerTypeIfPresent(
            classLoader,
            "org.hibernate.generator.internal.CurrentTimestampGeneration",
            MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS,
        )
    }
}
