package io.flavien.demo.api.user.mapper

import io.flavien.demo.api.dto.UserDto
import io.flavien.demo.api.dto.UserUpdateAdminDto
import io.flavien.demo.domain.user.entity.User
import org.mapstruct.Mapper

@Mapper(componentModel="spring")
interface UserMapper {

    fun toUserDto(user: User): UserDto

    fun fromUserDto(userDto: UserDto): User

    fun fromUserUpdateDto(userUpdateAdminDto: UserUpdateAdminDto): User

    fun fromUserUpdateAdminDto(userUpdateAdminDto: UserUpdateAdminDto): UserDto
}