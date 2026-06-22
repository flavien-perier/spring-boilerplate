package io.flavien.demo.domain.permission.init

import io.flavien.demo.domain.permission.entity.Permission
import io.flavien.demo.domain.permission.model.PermissionEnum
import io.flavien.demo.domain.permission.repository.PermissionRepository
import io.flavien.demo.domain.tenant.model.DbDefinition
import io.flavien.demo.domain.tenant.model.RedisDefinition
import io.flavien.demo.domain.tenant.model.SmtpDefinition
import io.flavien.demo.domain.tenant.model.TenantDefinition
import io.flavien.demo.domain.tenant.repository.TenantRegistry
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class DataInitializerTest {
    @InjectMocks
    var dataInitializer: DataInitializer? = null

    @Mock
    var permissionRepository: PermissionRepository? = null

    @Mock
    var tenantRegistry: TenantRegistry? = null

    @Test
    fun `Should create only the missing permissions`() {
        // Given
        Mockito
            .`when`(tenantRegistry!!.getAll())
            .thenReturn(
                mapOf(
                    "test-tenant" to
                        TenantDefinition(
                            "test-tenant",
                            DbDefinition("jdbc:postgresql://localhost:5432/test", "user", "pass", "public"),
                            RedisDefinition("localhost", 6379),
                            SmtpDefinition("localhost", 25, accountCreator = "noreply@test.com", domainLinks = "http://localhost"),
                        ),
                ),
            )
        Mockito
            .`when`(permissionRepository!!.existsById(PermissionEnum.MANAGE_OWN_ACCOUNT))
            .thenReturn(false)
        Mockito
            .`when`(permissionRepository!!.existsById(PermissionEnum.MANAGE_OWN_SESSIONS))
            .thenReturn(true)
        Mockito
            .`when`(permissionRepository!!.existsById(PermissionEnum.MANAGE_ALL_USERS))
            .thenReturn(false)

        // When
        dataInitializer!!.initialize()

        // Then
        Mockito.verify(permissionRepository!!).save(Permission(PermissionEnum.MANAGE_OWN_ACCOUNT))
        Mockito
            .verify(permissionRepository!!, Mockito.never())
            .save(Permission(PermissionEnum.MANAGE_OWN_SESSIONS))
        Mockito.verify(permissionRepository!!).save(Permission(PermissionEnum.MANAGE_ALL_USERS))
    }
}
