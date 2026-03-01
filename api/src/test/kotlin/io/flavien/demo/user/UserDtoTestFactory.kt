package io.flavien.demo.user

import io.flavien.demo.dto.*

object UserDtoTestFactory {

    fun initUserDto() = UserDto("perier@flavien.io")

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
    ): UserUpdateAdminDto {
        return UserUpdateAdminDto(email, password, proofOfWork, role)
    }

    fun initUserUpdateDto(
        email: String = "perier@flavien.io",
        password: String = "newPassword",
        proofOfWork: String = "proofOfWork",
    ) = UserUpdateDto(email, password, proofOfWork)
}
