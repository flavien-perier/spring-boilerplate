package io.flavien.demo.user

import io.flavien.demo.dto.*
import io.flavien.demo.user.entity.ForgotPassword
import io.flavien.demo.user.entity.User
import io.flavien.demo.user.entity.UserActivation
import io.flavien.demo.user.model.UserRole
import io.flavien.demo.user.model.UserUpdate

object UserTestFactory {

    fun initUserDto() = UserDto("perier@flavien.io")

    fun initUser(
        email: String = "perier@flavien.io",
        password: String = "password",
        proofOfWork: String = "proofOfWork",
        passwordSalt: String = "salt",
        role: UserRole = UserRole.USER,
        enabled: Boolean = true,
        id: Long? = null,
    ): User {
        val user = User(email, password, proofOfWork, passwordSalt, role, enabled)
        if (id != null) {
            user.id = id
        }
        return user
    }

    fun initUserCreationDto(
        email: String = "perier@flavien.io",
        password: String = "password",
        proofOfWork: String = "proofOfWork",
    ) = UserCreationDto(email, password, proofOfWork)

    fun initChangePasswordDto(
        token: String = "token",
        newPassword: String = "newPassword",
        proofOfWork: String = "proofOfWork",
    ) = ChangePasswordDto(token, newPassword, proofOfWork)

    fun initUserUpdateAdminDto(
        email: String = "perier@flavien.io",
        password: String = "newPassword",
        proofOfWork: String = "proofOfWork",
        role: Role = Role.USER,
    ): UserUpdateAdminDto {
        val userUpdateAdminDto = UserUpdateAdminDto(email, password, proofOfWork)
        userUpdateAdminDto.role = role
        return userUpdateAdminDto
    }

    fun initUserUpdateDto(
        email: String = "perier@flavien.io",
        password: String = "newPassword",
        proofOfWork: String = "proofOfWork",
    ) = UserUpdateDto(email, password, proofOfWork)

    fun initUserUpdate(
        email: String = "perier@flavien.io",
        password: String = "newPassword",
        proofOfWork: String = "proofOfWork",
        role: UserRole = UserRole.USER,
    ) = UserUpdate(email, password, proofOfWork, role)

    fun initUserActivation(
        token: String = "activationToken",
        userId: Long = 1L,
    ) = UserActivation(token, userId)

    fun initForgotPassword(
        token: String = "forgotPasswordToken",
        userId: Long = 1L,
    ) = ForgotPassword(token, userId)
}
