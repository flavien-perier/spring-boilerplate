package io.flavien.demo.api.user

import io.flavien.demo.api.generated.api.UserApi
import io.flavien.demo.api.generated.dto.ChangePasswordDto
import io.flavien.demo.api.generated.dto.ForgotPasswordDto
import io.flavien.demo.api.generated.dto.OtpConfirmDto
import io.flavien.demo.api.generated.dto.OtpSetupDto
import io.flavien.demo.api.generated.dto.UserCreationDto
import io.flavien.demo.api.generated.dto.UserDto
import io.flavien.demo.api.generated.dto.UserExportDto
import io.flavien.demo.api.generated.dto.UserPageDto
import io.flavien.demo.api.generated.dto.UserUpdateAdminDto
import io.flavien.demo.api.generated.dto.UserUpdateDto
import io.flavien.demo.api.session.util.ContextUtil
import io.flavien.demo.api.user.mapper.UserMapper
import io.flavien.demo.api.user.mapper.UserUpdateMapper
import io.flavien.demo.domain.user.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller

@Controller
class UserController(
    private val userService: UserService,
    private val userMapper: UserMapper,
    private val userUpdateMapper: UserUpdateMapper,
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
        val user = userService.get(userMail)

        return ResponseEntity.ok(userMapper.toUserDto(user))
    }

    override fun updateUser(
        userMail: String,
        userUpdateAdminDto: UserUpdateAdminDto,
    ): ResponseEntity<UserDto> {
        val user = userService.update(userMail, userUpdateMapper.fromUserUpdateAdminDto(userUpdateAdminDto))

        return ResponseEntity.ok(userMapper.toUserDto(user))
    }

    override fun deleteUser(userMail: String): ResponseEntity<Unit> {
        userService.delete(userMail)

        return ResponseEntity.noContent().build()
    }

    override fun getCurrentUser(): ResponseEntity<UserDto> {
        val user = userService.get(ContextUtil.userId)

        return ResponseEntity.ok(userMapper.toUserDto(user))
    }

    override fun updateCurrentUser(userUpdateDto: UserUpdateDto): ResponseEntity<UserDto> {
        val user = userService.update(ContextUtil.userId, userUpdateMapper.fromUserUpdateDto(userUpdateDto))

        return ResponseEntity.ok(userMapper.toUserDto(user))
    }

    override fun deleteCurrentUser(): ResponseEntity<Unit> {
        userService.delete(ContextUtil.userId)
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
        val user = userService.get(ContextUtil.userId)

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

        val userPageDto =
            UserPageDto(
                users.totalElements,
                users.totalPages,
                users.content.map { userMapper.toUserDto(it) },
            )

        return ResponseEntity.ok(userPageDto)
    }
}
