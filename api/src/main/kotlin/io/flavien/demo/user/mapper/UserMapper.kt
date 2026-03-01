package io.flavien.demo.user.mapper

import io.flavien.demo.dto.UserDto
import io.flavien.demo.dto.UserUpdateAdminDto
import io.flavien.demo.user.entity.User
import org.mapstruct.Mapper

@Mapper(componentModel="spring")
interface UserMapper {

    fun toUserDto(user: User): UserDto

    fun fromUserDto(userDto: UserDto): User

    fun fromUserUpdateDto(userUpdateAdminDto: UserUpdateAdminDto): User

    fun fromUserUpdateAdminDto(userUpdateAdminDto: UserUpdateAdminDto): UserDto
}