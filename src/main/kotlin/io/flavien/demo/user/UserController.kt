package io.flavien.demo.user

import io.flavien.demo.api.UserApi
import io.flavien.demo.dto.*
import io.flavien.demo.session.util.ContextUtil
import io.flavien.demo.user.mapper.UserMapper
import io.flavien.demo.user.mapper.UserUpdateMapper
import io.flavien.demo.user.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller

@Controller
class UserController(
    private val userService: UserService,
    private val userMapper: UserMapper,
    private val userUpdateMapper: UserUpdateMapper,
) : UserApi {

    override fun createUser(userCreationDto: UserCreationDto): ResponseEntity<Void> {
        userService.create(userCreationDto.email, userCreationDto.password, userCreationDto.proofOfWork)

        return ResponseEntity(
            HttpStatus.NO_CONTENT,
        )
    }

    override fun activateUser(token: String): ResponseEntity<Void> {
        userService.activate(token)

        return ResponseEntity(
            HttpStatus.NO_CONTENT,
        )
    }

    override fun forgotPassword(email: String): ResponseEntity<Void> {
        userService.sendForgotPassword(email)

        return ResponseEntity(
            HttpStatus.NO_CONTENT,
        )
    }

    override fun updatePassword(changePasswordDto: ChangePasswordDto): ResponseEntity<Void> {
        userService.updatePassword(changePasswordDto.password, changePasswordDto.proofOfWork, changePasswordDto.token)

        return ResponseEntity(
            HttpStatus.NO_CONTENT,
        )
    }

    override fun getUser(userMail: String): ResponseEntity<UserDto> {
        val user = userService.get(userMail)

        return ResponseEntity(
            userMapper.toUserDto(user),
            HttpStatus.OK,
        )
    }

    override fun updateUser(userMail: String, userUpdateAdminDto: UserUpdateAdminDto): ResponseEntity<UserDto> {
        val user = userService.update(userMail, userUpdateMapper.fromUserUpdateAdminDto(userUpdateAdminDto))

        return ResponseEntity(
            userMapper.toUserDto(user),
            HttpStatus.OK,
        )
    }

    override fun deleteUser(userMail: String): ResponseEntity<Void> {
        userService.delete(userMail)

        return ResponseEntity(HttpStatus.NO_CONTENT)
    }

    override fun getUserMe(): ResponseEntity<UserDto> {
        val user = userService.get(ContextUtil.userId)

        return ResponseEntity(
            userMapper.toUserDto(user),
            HttpStatus.OK,
        )
    }

    override fun updateUserMe(userUpdateDto: UserUpdateDto): ResponseEntity<UserDto> {
        val user = userService.update(ContextUtil.userId, userUpdateMapper.fromUserUpdateDto(userUpdateDto))

        return ResponseEntity(
            userMapper.toUserDto(user),
            HttpStatus.OK,
        )
    }

    override fun deleteUserMe(): ResponseEntity<Void> {
        userService.delete(ContextUtil.userId)
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }

    override fun findUsers(
        q: String?,
        page: Int?,
        pageSize: Int?,
        sortColumn: String?,
        sortOrder: String?,
    ): ResponseEntity<UserPageDto> {
        val users = userService.find(q, page, pageSize, sortColumn, sortOrder)

        val userPageDto = UserPageDto(
            users.totalElements,
            users.totalPages,
            users.content.map { userMapper.toUserDto(it) },
        )

        return ResponseEntity(
            userPageDto,
            HttpStatus.OK,
        )
    }
}