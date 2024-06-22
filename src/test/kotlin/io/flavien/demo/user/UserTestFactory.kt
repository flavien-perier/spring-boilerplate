package io.flavien.demo.user

import io.flavien.demo.dto.*
import io.flavien.demo.user.entity.User
import io.flavien.demo.user.model.UserRole
import io.flavien.demo.user.model.UserUpdate

object UserTestFactory {

    fun initUserDto() = UserDto("perier@flavien.io")

    fun initUser() = User("perier@flavien.io", "password", "salt", UserRole.USER, true)

    fun initUserCreationDto() = UserCreationDto("perier@flavien.io", "password")

    fun initChangePasswordDto() = ChangePasswordDto("token", "newPassword")

    fun initUserUpdateAdminDto(): UserUpdateAdminDto {
        val userUpdateAdminDto = UserUpdateAdminDto("perier@flavien.io", "newPassword")
        userUpdateAdminDto.role = Role.USER
        return userUpdateAdminDto
    }

    fun initUserUpdateDto() = UserUpdateDto("perier@flavien.io", "newPassword")

    fun initUserUpdate() = UserUpdate("perier@flavien.io", "newPassword", UserRole.USER)
}