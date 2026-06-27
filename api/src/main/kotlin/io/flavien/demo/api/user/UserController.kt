package io.flavien.demo.api.user

import io.flavien.demo.api.generated.api.UserApi
import io.flavien.demo.api.generated.dto.ChangePasswordDto
import io.flavien.demo.api.generated.dto.ForgotPasswordDto
import io.flavien.demo.api.generated.dto.GroupDto
import io.flavien.demo.api.generated.dto.GroupPageDto
import io.flavien.demo.api.generated.dto.OtpConfirmDto
import io.flavien.demo.api.generated.dto.OtpSetupDto
import io.flavien.demo.api.generated.dto.PermissionSettingDto
import io.flavien.demo.api.generated.dto.PermissionSettingPageDto
import io.flavien.demo.api.generated.dto.PermissionUpdateDto
import io.flavien.demo.api.generated.dto.UserCreationDto
import io.flavien.demo.api.generated.dto.UserDto
import io.flavien.demo.api.generated.dto.UserExportDto
import io.flavien.demo.api.generated.dto.UserPageDto
import io.flavien.demo.api.generated.dto.UserUpdateAdminDto
import io.flavien.demo.api.generated.dto.UserUpdateDto
import io.flavien.demo.api.group.mapper.GroupMapper
import io.flavien.demo.api.permission.mapper.PermissionMapper
import io.flavien.demo.api.session.util.ContextUtil
import io.flavien.demo.api.user.mapper.UserMapper
import io.flavien.demo.api.user.mapper.UserUpdateMapper
import io.flavien.demo.domain.group.service.GroupService
import io.flavien.demo.domain.permission.model.PermissionEnum
import io.flavien.demo.domain.permission.service.PermissionService
import io.flavien.demo.domain.shared.util.PageableUtil
import io.flavien.demo.domain.user.service.UserService
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import java.util.UUID

@Controller
class UserController(
    private val userService: UserService,
    private val userMapper: UserMapper,
    private val userUpdateMapper: UserUpdateMapper,
    private val permissionService: PermissionService,
    private val permissionMapper: PermissionMapper,
    private val groupService: GroupService,
    private val groupMapper: GroupMapper,
) : UserApi {
    override fun createUser(userCreationDto: UserCreationDto): ResponseEntity<Unit> {
        userService.create(userCreationDto.email, userCreationDto.password, userCreationDto.proofOfWork)

        return ResponseEntity.noContent().build()
    }

    override fun activateUser(token: String): ResponseEntity<Unit> {
        userService.activate(token)

        return ResponseEntity.noContent().build()
    }

    override fun forgotPassword(forgotPasswordDto: ForgotPasswordDto): ResponseEntity<Unit> {
        userService.sendForgotPassword(forgotPasswordDto.email)

        return ResponseEntity.noContent().build()
    }

    override fun updatePassword(changePasswordDto: ChangePasswordDto): ResponseEntity<Unit> {
        userService.updatePassword(changePasswordDto.password, changePasswordDto.proofOfWork, changePasswordDto.token)

        return ResponseEntity.noContent().build()
    }

    override fun getUser(userMail: String): ResponseEntity<UserDto> {
        val user = userService.getByEmail(userMail)

        return ResponseEntity.ok(userMapper.toUserDto(user))
    }

    override fun updateUser(
        userMail: String,
        userUpdateAdminDto: UserUpdateAdminDto,
    ): ResponseEntity<UserDto> {
        val user = userService.updateByEmail(userMail, userUpdateMapper.fromUserUpdateAdminDto(userUpdateAdminDto))

        return ResponseEntity.ok(userMapper.toUserDto(user))
    }

    override fun deleteUser(userMail: String): ResponseEntity<Unit> {
        userService.deleteByEmail(userMail)

        return ResponseEntity.noContent().build()
    }

    override fun getCurrentUser(): ResponseEntity<UserDto> {
        val user = userService.getById(ContextUtil.userId)

        return ResponseEntity.ok(userMapper.toUserDto(user))
    }

    override fun updateCurrentUser(userUpdateDto: UserUpdateDto): ResponseEntity<UserDto> {
        val user = userService.updateById(ContextUtil.userId, userUpdateMapper.fromUserUpdateDto(userUpdateDto))

        return ResponseEntity.ok(userMapper.toUserDto(user))
    }

    override fun deleteCurrentUser(): ResponseEntity<Unit> {
        userService.deleteById(ContextUtil.userId)
        return ResponseEntity.noContent().build()
    }

    override fun setupCurrentUserOtp(): ResponseEntity<OtpSetupDto> {
        val uri = userService.setupOtp(ContextUtil.userId)
        return ResponseEntity.ok(OtpSetupDto(uri))
    }

    override fun confirmCurrentUserOtp(otpConfirmDto: OtpConfirmDto): ResponseEntity<Unit> {
        userService.confirmOtp(ContextUtil.userId, otpConfirmDto.otp)
        return ResponseEntity.noContent().build()
    }

    override fun disableCurrentUserOtp(): ResponseEntity<Unit> {
        userService.disableOtp(ContextUtil.userId)
        return ResponseEntity.noContent().build()
    }

    override fun exportCurrentUserData(): ResponseEntity<UserExportDto> {
        val user = userService.getById(ContextUtil.userId)

        return ResponseEntity.ok(userMapper.toUserExportDto(user))
    }

    override fun findUsers(
        q: String?,
        page: Int?,
        pageSize: Int?,
        sortColumn: String?,
        sortOrder: String?,
    ): ResponseEntity<UserPageDto> {
        val users = userService.find(q, page, pageSize, sortColumn, sortOrder)
        return ResponseEntity.ok(userMapper.toUserPageDto(users))
    }

    override fun getUserPermissions(): ResponseEntity<List<String>> {
        val permissions = permissionService.getGrantedPermissions(ContextUtil.userId)
        return ResponseEntity.ok(permissions.map { it.name })
    }

    override fun getUserPermissionOverrides(
        userMail: String,
        page: Int?,
        pageSize: Int?,
        sortColumn: String?,
        sortOrder: String?,
    ): ResponseEntity<PermissionSettingPageDto> {
        val user = userService.getByEmail(userMail)
        val pageable = PageableUtil.toPageable(page, pageSize, sortColumn, sortOrder, "permission")
        val settings = permissionService.getUserPermissionOverrides(user.id!!, pageable)
        return ResponseEntity.ok(permissionMapper.toPermissionSettingPageDto(settings))
    }

    override fun setUserPermission(
        userMail: String,
        permission: String,
        permissionUpdateDto: PermissionUpdateDto,
    ): ResponseEntity<Unit> {
        val user = userService.getByEmail(userMail)
        val permissionEnum = PermissionEnum.fromName(permission)
        permissionService.setUserPermission(user.id!!, permissionEnum, permissionUpdateDto.allow)
        return ResponseEntity.noContent().build()
    }

    override fun removeUserPermission(
        userMail: String,
        permission: String,
    ): ResponseEntity<Unit> {
        val user = userService.getByEmail(userMail)
        val permissionEnum = PermissionEnum.fromName(permission)
        permissionService.removeUserPermission(user.id!!, permissionEnum)
        return ResponseEntity.noContent().build()
    }

    override fun getUserGroups(
        userMail: String,
        page: Int?,
        pageSize: Int?,
        sortColumn: String?,
        sortOrder: String?,
    ): ResponseEntity<GroupPageDto> {
        val user = userService.getByEmail(userMail)
        val pageable = PageableUtil.toPageable(page, pageSize, sortColumn, sortOrder, "name")
        val groups = groupService.getUserGroups(user.id!!, pageable)
        return ResponseEntity.ok(groupMapper.toGroupPageDto(groups))
    }

    override fun addUserToGroup(
        userMail: String,
        groupId: String,
    ): ResponseEntity<Unit> {
        val user = userService.getByEmail(userMail)
        groupService.addUserToGroup(user.id!!, UUID.fromString(groupId))
        return ResponseEntity.noContent().build()
    }

    override fun removeUserFromGroup(
        userMail: String,
        groupId: String,
    ): ResponseEntity<Unit> {
        val user = userService.getByEmail(userMail)
        groupService.removeUserFromGroup(user.id!!, UUID.fromString(groupId))
        return ResponseEntity.noContent().build()
    }

    override fun disableUserOtp(userMail: String): ResponseEntity<Unit> {
        val user = userService.getByEmail(userMail)
        userService.disableOtp(user.id!!)
        return ResponseEntity.noContent().build()
    }

    override fun sendUserPasswordReset(userMail: String): ResponseEntity<Unit> {
        userService.sendForgotPassword(userMail)
        return ResponseEntity.noContent().build()
    }
}
