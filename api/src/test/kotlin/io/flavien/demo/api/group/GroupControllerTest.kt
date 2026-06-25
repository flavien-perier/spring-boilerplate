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
import java.util.UUID

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

    companion object {
        private val GROUP_ID = UUID.fromString("00000000-0000-7000-8000-000000000001")
        private val GROUP_1_STR = GROUP_ID.toString()
    }

    @Test
    fun `Test createGroup`() {
        val dto = GroupDtoTestFactory.initGroupCreationDto()
        val group = GroupTestFactory.initGroup(name = "GROUP")
        val groupDto = GroupDtoTestFactory.initGroupDto(name = "GROUP")

        Mockito.`when`(groupService!!.create(dto.name, dto.parentId?.let { UUID.fromString(it) })).thenReturn(group)
        Mockito.`when`(groupMapper!!.toGroupDto(group)).thenReturn(groupDto)

        val response = groupController!!.createGroup(dto)

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body).isEqualTo(groupDto)
        Mockito.verify(groupService!!).create(dto.name, dto.parentId?.let { UUID.fromString(it) })
    }

    @Test
    fun `Test findGroups`() {
        val group1 =
            GroupTestFactory.initGroup(
                id = UUID.fromString("00000000-0000-7000-8000-000000000001"),
                name = "G1",
            )
        val group2 =
            GroupTestFactory.initGroup(
                id = UUID.fromString("00000000-0000-7000-8000-000000000002"),
                name = "G2",
            )
        val dto1 = GroupDtoTestFactory.initGroupDto(name = "G1")
        val dto2 = GroupDtoTestFactory.initGroupDto(name = "G2")

        Mockito.`when`(groupService!!.findAll()).thenReturn(listOf(group1, group2))
        Mockito.`when`(groupMapper!!.toGroupDto(group1)).thenReturn(dto1)
        Mockito.`when`(groupMapper!!.toGroupDto(group2)).thenReturn(dto2)

        val response = groupController!!.findGroups()

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body).containsExactly(dto1, dto2)
    }

    @Test
    fun `Test getGroup`() {
        val group = GroupTestFactory.initGroup(name = "GROUP")
        val dto = GroupDtoTestFactory.initGroupDto(name = "GROUP")

        Mockito.`when`(groupService!!.getById(GROUP_ID)).thenReturn(group)
        Mockito.`when`(groupMapper!!.toGroupDto(group)).thenReturn(dto)

        val response = groupController!!.getGroup(GROUP_1_STR)

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body).isEqualTo(dto)
    }

    @Test
    fun `Test updateGroup`() {
        val updateDto = GroupDtoTestFactory.initGroupUpdateDto(name = "UPDATED")
        val group = GroupTestFactory.initGroup(name = "UPDATED")
        val dto = GroupDtoTestFactory.initGroupDto(name = "UPDATED")

        Mockito
            .`when`(
                groupService!!.update(GROUP_ID, updateDto.name, updateDto.parentId?.let { UUID.fromString(it) }),
            ).thenReturn(group)
        Mockito.`when`(groupMapper!!.toGroupDto(group)).thenReturn(dto)

        val response = groupController!!.updateGroup(GROUP_1_STR, updateDto)

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body).isEqualTo(dto)
    }

    @Test
    fun `Test deleteGroup`() {
        val response = groupController!!.deleteGroup(GROUP_1_STR)

        assertThat(response.statusCode).isEqualTo(HttpStatus.NO_CONTENT)
        Mockito.verify(groupService!!).delete(GROUP_ID)
    }

    @Test
    fun `Test getGroupPermissions`() {
        val settings =
            listOf(
                PermissionSetting(PermissionEnum.MANAGE_ALL_USERS, true),
                PermissionSetting(PermissionEnum.MANAGE_ALL_GROUPS, null),
            )

        Mockito.`when`(permissionService!!.getGroupPermissions(GROUP_ID)).thenReturn(settings)

        val response = groupController!!.getGroupPermissions(GROUP_1_STR)

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

        val response = groupController!!.setGroupPermission(GROUP_1_STR, "MANAGE_ALL_USERS", updateDto)

        assertThat(response.statusCode).isEqualTo(HttpStatus.NO_CONTENT)
        Mockito.verify(permissionService!!).setGroupPermission(GROUP_ID, PermissionEnum.MANAGE_ALL_USERS, true)
    }

    @Test
    fun `Test removeGroupPermission`() {
        val response = groupController!!.removeGroupPermission(GROUP_1_STR, "MANAGE_ALL_USERS")

        assertThat(response.statusCode).isEqualTo(HttpStatus.NO_CONTENT)
        Mockito.verify(permissionService!!).removeGroupPermission(GROUP_ID, PermissionEnum.MANAGE_ALL_USERS)
    }
}
