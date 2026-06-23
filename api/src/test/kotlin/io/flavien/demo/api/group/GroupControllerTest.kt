package io.flavien.demo.api.group

import io.flavien.demo.api.group.mapper.GroupMapper
import io.flavien.demo.domain.group.service.GroupService
import io.flavien.demo.domain.permission.model.PermissionEnum
import io.flavien.demo.domain.permission.model.PermissionSetting
import io.flavien.demo.domain.permission.service.PermissionService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.http.HttpStatus

@ExtendWith(MockitoExtension::class)
class GroupControllerTest {
    @InjectMocks
    var groupController: GroupController? = null

    @Mock
    var groupService: GroupService? = null

    @Mock
    var permissionService: PermissionService? = null

    @Mock
    var groupMapper: GroupMapper? = null

    @Test
    fun `Test createGroup`() {
        val dto = GroupDtoTestFactory.initGroupCreationDto()
        val group = GroupTestFactory.initGroup(id = 1L, name = "GROUP")
        val groupDto = GroupDtoTestFactory.initGroupDto(id = 1L, name = "GROUP")

        Mockito.`when`(groupService!!.create(dto.name, dto.parentId)).thenReturn(group)
        Mockito.`when`(groupMapper!!.toGroupDto(group)).thenReturn(groupDto)

        val response = groupController!!.createGroup(dto)

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body).isEqualTo(groupDto)
        Mockito.verify(groupService!!).create(dto.name, dto.parentId)
    }

    @Test
    fun `Test findGroups`() {
        val group1 = GroupTestFactory.initGroup(id = 1L, name = "G1")
        val group2 = GroupTestFactory.initGroup(id = 2L, name = "G2")
        val dto1 = GroupDtoTestFactory.initGroupDto(id = 1L, name = "G1")
        val dto2 = GroupDtoTestFactory.initGroupDto(id = 2L, name = "G2")

        Mockito.`when`(groupService!!.findAll()).thenReturn(listOf(group1, group2))
        Mockito.`when`(groupMapper!!.toGroupDto(group1)).thenReturn(dto1)
        Mockito.`when`(groupMapper!!.toGroupDto(group2)).thenReturn(dto2)

        val response = groupController!!.findGroups()

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body).containsExactly(dto1, dto2)
    }

    @Test
    fun `Test getGroup`() {
        val group = GroupTestFactory.initGroup(id = 1L, name = "GROUP")
        val dto = GroupDtoTestFactory.initGroupDto(id = 1L, name = "GROUP")

        Mockito.`when`(groupService!!.get(1L)).thenReturn(group)
        Mockito.`when`(groupMapper!!.toGroupDto(group)).thenReturn(dto)

        val response = groupController!!.getGroup(1L)

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body).isEqualTo(dto)
    }

    @Test
    fun `Test updateGroup`() {
        val updateDto = GroupDtoTestFactory.initGroupUpdateDto(name = "UPDATED")
        val group = GroupTestFactory.initGroup(id = 1L, name = "UPDATED")
        val dto = GroupDtoTestFactory.initGroupDto(id = 1L, name = "UPDATED")

        Mockito.`when`(groupService!!.update(1L, updateDto.name, updateDto.parentId)).thenReturn(group)
        Mockito.`when`(groupMapper!!.toGroupDto(group)).thenReturn(dto)

        val response = groupController!!.updateGroup(1L, updateDto)

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body).isEqualTo(dto)
    }

    @Test
    fun `Test deleteGroup`() {
        val response = groupController!!.deleteGroup(1L)

        assertThat(response.statusCode).isEqualTo(HttpStatus.NO_CONTENT)
        Mockito.verify(groupService!!).delete(1L)
    }

    @Test
    fun `Test getGroupPermissions`() {
        val settings =
            listOf(
                PermissionSetting(PermissionEnum.MANAGE_ALL_USERS, true),
                PermissionSetting(PermissionEnum.MANAGE_ALL_GROUPS, null),
            )

        Mockito.`when`(permissionService!!.getGroupPermissions(1L)).thenReturn(settings)

        val response = groupController!!.getGroupPermissions(1L)

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body).hasSize(2)
        assertThat(response.body!![0].permission).isEqualTo("MANAGE_ALL_USERS")
        assertThat(response.body!![0].allow).isTrue()
        assertThat(response.body!![1].permission).isEqualTo("MANAGE_ALL_GROUPS")
        assertThat(response.body!![1].allow).isNull()
    }

    @Test
    fun `Test setGroupPermission`() {
        val updateDto = GroupDtoTestFactory.initPermissionUpdateDto(allow = true)

        val response = groupController!!.setGroupPermission(1L, "MANAGE_ALL_USERS", updateDto)

        assertThat(response.statusCode).isEqualTo(HttpStatus.NO_CONTENT)
        Mockito.verify(permissionService!!).setGroupPermission(1L, PermissionEnum.MANAGE_ALL_USERS, true)
    }

    @Test
    fun `Test removeGroupPermission`() {
        val response = groupController!!.removeGroupPermission(1L, "MANAGE_ALL_USERS")

        assertThat(response.statusCode).isEqualTo(HttpStatus.NO_CONTENT)
        Mockito.verify(permissionService!!).removeGroupPermission(1L, PermissionEnum.MANAGE_ALL_USERS)
    }
}
