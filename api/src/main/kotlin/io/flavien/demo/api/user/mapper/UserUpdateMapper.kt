package io.flavien.demo.api.user.mapper

import io.flavien.demo.api.generated.dto.UserUpdateAdminDto
import io.flavien.demo.api.generated.dto.UserUpdateDto
import io.flavien.demo.domain.user.model.UserUpdate
import org.mapstruct.Mapper

@Mapper(componentModel = "spring")
interface UserUpdateMapper {
    fun fromUserUpdateDto(userUpdateDto: UserUpdateDto): UserUpdate

    fun fromUserUpdateAdminDto(userUpdateAdminDto: UserUpdateAdminDto): UserUpdate
}
