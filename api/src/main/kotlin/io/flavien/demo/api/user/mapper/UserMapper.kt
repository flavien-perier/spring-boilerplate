package io.flavien.demo.api.user.mapper

import io.flavien.demo.api.generated.dto.UserDto
import io.flavien.demo.api.generated.dto.UserUpdateAdminDto
import io.flavien.demo.domain.user.entity.User
import org.mapstruct.Mapper
import org.mapstruct.Mapping

@Mapper(componentModel = "spring")
interface UserMapper {
    @Mapping(target = "otpEnabled", expression = "java(user.getOtpSecret() != null)")
    fun toUserDto(user: User): UserDto

    @Mapping(target = "otpSecret", ignore = true)
    fun fromUserDto(userDto: UserDto): User

    @Mapping(target = "otpSecret", ignore = true)
    fun fromUserUpdateDto(userUpdateAdminDto: UserUpdateAdminDto): User

    fun fromUserUpdateAdminDto(userUpdateAdminDto: UserUpdateAdminDto): UserDto
}
