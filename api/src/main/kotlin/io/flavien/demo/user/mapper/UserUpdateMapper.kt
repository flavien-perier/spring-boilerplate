package io.flavien.demo.user.mapper

import io.flavien.demo.dto.UserUpdateAdminDto
import io.flavien.demo.dto.UserUpdateDto
import io.flavien.demo.user.model.UserUpdate
import org.mapstruct.Mapper

@Mapper(componentModel="spring")
interface UserUpdateMapper {

    fun fromUserUpdateDto(userUpdateDto: UserUpdateDto): UserUpdate

    fun fromUserUpdateAdminDto(userUpdateAdminDto: UserUpdateAdminDto): UserUpdate
}