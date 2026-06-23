package io.flavien.demo.api.user

import io.flavien.demo.api.generated.dto.ChangePasswordDto
import io.flavien.demo.api.generated.dto.UserCreationDto
import io.flavien.demo.api.generated.dto.UserDto
import io.flavien.demo.api.generated.dto.UserUpdateAdminDto
import io.flavien.demo.api.generated.dto.UserUpdateDto

object UserDtoTestFactory {
    fun initUserDto() = UserDto("perier@flavien.io", otpEnabled = false, enabled = true)

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
        enabled: Boolean? = null,
    ): UserUpdateAdminDto = UserUpdateAdminDto(email, password, proofOfWork, enabled)

    fun initUserUpdateDto(
        email: String = "perier@flavien.io",
        password: String = "newPassword",
        proofOfWork: String = "proofOfWork",
    ) = UserUpdateDto(email, password, proofOfWork)
}
