package io.flavien.demo.api.user

import io.flavien.demo.api.generated.dto.ChangePasswordDto
import io.flavien.demo.api.generated.dto.UserCreationDto
import io.flavien.demo.api.generated.dto.UserDto
import io.flavien.demo.api.generated.dto.UserExportDto
import io.flavien.demo.api.generated.dto.UserUpdateAdminDto
import io.flavien.demo.api.generated.dto.UserUpdateDto
import java.time.OffsetDateTime

object UserDtoTestFactory {
    fun initUserDto() = UserDto("perier@flavien.io", otpEnabled = false, enabled = true)

    fun initUserExportDto(
        email: String = "perier@flavien.io",
        enabled: Boolean = true,
        lastLogin: OffsetDateTime = OffsetDateTime.parse("2024-01-01T00:00:00Z"),
        creationDate: OffsetDateTime = OffsetDateTime.parse("2024-01-01T00:00:00Z"),
        updateDate: OffsetDateTime = OffsetDateTime.parse("2024-01-01T00:00:00Z"),
        otpEnabled: Boolean = false,
        roles: List<String> = listOf("ADMIN", "USER"),
        permissions: List<String> = listOf("MANAGE_ALL_USERS", "MANAGE_ALL_ROLES"),
    ) = UserExportDto(
        email = email,
        enabled = enabled,
        lastLogin = lastLogin,
        creationDate = creationDate,
        updateDate = updateDate,
        otpEnabled = otpEnabled,
        roles = roles,
        permissions = permissions,
    )

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
