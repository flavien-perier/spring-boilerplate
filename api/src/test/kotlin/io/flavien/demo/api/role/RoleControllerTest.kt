package io.flavien.demo.api.role

import io.flavien.demo.api.generated.dto.RolePageDto
import io.flavien.demo.api.permission.mapper.PermissionMapper
import io.flavien.demo.api.role.mapper.RoleMapper
import io.flavien.demo.domain.permission.model.PermissionEnum
import io.flavien.demo.domain.permission.model.PermissionSetting
import io.flavien.demo.domain.permission.service.PermissionService
import io.flavien.demo.domain.role.service.RoleService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import java.util.UUID

@ExtendWith(MockitoExtension::class)
class RoleControllerTest {
    @InjectMocks
    var roleController: RoleController? = null

    @Mock
    var roleService: RoleService? = null

    @Mock
    var permissionService: PermissionService? = null

    @Mock
    var roleMapper: RoleMapper? = null

    @Mock
    var permissionMapper: PermissionMapper? = null

    companion object {
        private val ROLE_ID = UUID.fromString("00000000-0000-7000-8000-000000000001")
        private val ROLE_1_STR = ROLE_ID.toString()
    }

    @Test
    fun `Test createRole`() {
        val dto = RoleDtoTestFactory.initRoleCreationDto()
        val role = RoleTestFactory.initRole(name = "ROLE")
        val roleDto = RoleDtoTestFactory.initRoleDto(name = "ROLE")

        Mockito.`when`(roleService!!.create(dto.name, dto.parentId?.let { UUID.fromString(it) })).thenReturn(role)
        Mockito.`when`(roleMapper!!.toRoleDto(role)).thenReturn(roleDto)

        val response = roleController!!.createRole(dto)

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body).isEqualTo(roleDto)
        Mockito.verify(roleService!!).create(dto.name, dto.parentId?.let { UUID.fromString(it) })
    }

    @Test
    fun `Test findRoles`() {
        val page = 1
        val pageSize = 10
        val sortColumn = "name"
        val sortOrder = "ASC"
        val pageable =
            PageRequest.of(
                0,
                pageSize,
                org.springframework.data.domain.Sort
                    .by(
                        org.springframework.data.domain.Sort.Direction
                            .fromString(sortOrder),
                        sortColumn,
                    ),
            )

        val role1 =
            RoleTestFactory.initRole(
                id = UUID.fromString("00000000-0000-7000-8000-000000000001"),
                name = "G1",
            )
        val role2 =
            RoleTestFactory.initRole(
                id = UUID.fromString("00000000-0000-7000-8000-000000000002"),
                name = "G2",
            )
        val dto1 = RoleDtoTestFactory.initRoleDto(name = "G1")
        val dto2 = RoleDtoTestFactory.initRoleDto(name = "G2")
        val roles = PageImpl(listOf(role1, role2), pageable, 2)
        val expectedPageDto =
            RolePageDto(
                totalElements = 2,
                totalPages = 1,
                number = 0,
                propertySize = pageSize,
                content = listOf(dto1, dto2),
            )

        Mockito.`when`(roleService!!.findAll(pageable)).thenReturn(roles)
        Mockito.`when`(roleMapper!!.toRolePageDto(roles)).thenReturn(expectedPageDto)

        val response = roleController!!.findRoles(page, pageSize, sortColumn, sortOrder)

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body!!.content).containsExactly(dto1, dto2)
        assertThat(response.body!!.totalElements).isEqualTo(2)
        assertThat(response.body!!.totalPages).isEqualTo(1)
        assertThat(response.body!!.propertySize).isEqualTo(pageSize)
    }

    @Test
    fun `Test getRole`() {
        val role = RoleTestFactory.initRole(name = "ROLE")
        val dto = RoleDtoTestFactory.initRoleDto(name = "ROLE")

        Mockito.`when`(roleService!!.getById(ROLE_ID)).thenReturn(role)
        Mockito.`when`(roleMapper!!.toRoleDto(role)).thenReturn(dto)

        val response = roleController!!.getRole(ROLE_1_STR)

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body).isEqualTo(dto)
    }

    @Test
    fun `Test updateRole`() {
        val updateDto = RoleDtoTestFactory.initRoleUpdateDto(name = "UPDATED")
        val role = RoleTestFactory.initRole(name = "UPDATED")
        val dto = RoleDtoTestFactory.initRoleDto(name = "UPDATED")

        Mockito
            .`when`(
                roleService!!.update(ROLE_ID, updateDto.name, updateDto.parentId?.let { UUID.fromString(it) }),
            ).thenReturn(role)
        Mockito.`when`(roleMapper!!.toRoleDto(role)).thenReturn(dto)

        val response = roleController!!.updateRole(ROLE_1_STR, updateDto)

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body).isEqualTo(dto)
    }

    @Test
    fun `Test deleteRole`() {
        val response = roleController!!.deleteRole(ROLE_1_STR)

        assertThat(response.statusCode).isEqualTo(HttpStatus.NO_CONTENT)
        Mockito.verify(roleService!!).delete(ROLE_ID)
    }

    @Test
    fun `Test getRolePermissions`() {
        val settings =
            listOf(
                PermissionSetting(PermissionEnum.MANAGE_ALL_USERS, true),
                PermissionSetting(PermissionEnum.MANAGE_ALL_ROLES, null),
            )
        val dtos =
            listOf(
                RoleDtoTestFactory.initPermissionSettingDto(permission = "MANAGE_ALL_USERS", allow = true),
                RoleDtoTestFactory.initPermissionSettingDto(permission = "MANAGE_ALL_ROLES", allow = null),
            )

        Mockito.`when`(permissionService!!.getRolePermissions(ROLE_ID)).thenReturn(settings)
        Mockito.`when`(permissionMapper!!.toPermissionSettingDtoList(settings)).thenReturn(dtos)

        val response = roleController!!.getRolePermissions(ROLE_1_STR)

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body).hasSize(2)
        assertThat(response.body!![0].permission).isEqualTo("MANAGE_ALL_USERS")
        assertThat(response.body!![0].allow).isTrue()
        assertThat(response.body!![1].permission).isEqualTo("MANAGE_ALL_ROLES")
        assertThat(response.body!![1].allow).isNull()
    }

    @Test
    fun `Test setRolePermission`() {
        val updateDto = RoleDtoTestFactory.initPermissionUpdateDto(allow = true)

        val response = roleController!!.setRolePermission(ROLE_1_STR, "MANAGE_ALL_USERS", updateDto)

        assertThat(response.statusCode).isEqualTo(HttpStatus.NO_CONTENT)
        Mockito.verify(permissionService!!).setRolePermission(ROLE_ID, PermissionEnum.MANAGE_ALL_USERS, true)
    }

    @Test
    fun `Test removeRolePermission`() {
        val response = roleController!!.removeRolePermission(ROLE_1_STR, "MANAGE_ALL_USERS")

        assertThat(response.statusCode).isEqualTo(HttpStatus.NO_CONTENT)
        Mockito.verify(permissionService!!).removeRolePermission(ROLE_ID, PermissionEnum.MANAGE_ALL_USERS)
    }
}
