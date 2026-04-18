package io.flavien.demo.api.user

import io.flavien.demo.api.dto.ChangePasswordDto
import io.flavien.demo.api.dto.Role
import io.flavien.demo.api.dto.UserCreationDto
import io.flavien.demo.api.dto.UserDto
import io.flavien.demo.api.dto.UserUpdateAdminDto
import io.flavien.demo.api.dto.UserUpdateDto

object UserDtoTestFactory {
    fun initUserDto() = UserDto("perier@flavien.io", otpEnabled = false)

    fun initUserCreationDto(
        email: String = "perier@flavien.io",
        password: String = "Password123!",
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
    ): UserUpdateAdminDto = UserUpdateAdminDto(email, password, proofOfWork, role)

    fun initUserUpdateDto(
        email: String = "perier@flavien.io",
        password: String = "newPassword",
        proofOfWork: String = "proofOfWork",
    ) = UserUpdateDto(email, password, proofOfWork)
}
