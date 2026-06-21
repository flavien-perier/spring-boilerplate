package io.flavien.demo.api.user.mapper

import io.flavien.demo.api.generated.dto.UserDto
import io.flavien.demo.api.generated.dto.UserExportDto
import io.flavien.demo.domain.user.entity.User
import org.mapstruct.Mapper
import org.mapstruct.Mapping

@Mapper(componentModel = "spring")
interface UserMapper {
    @Mapping(target = "otpEnabled", expression = "java(user.getOtpSecret() != null)")
    fun toUserDto(user: User): UserDto

    @Mapping(target = "otpEnabled", expression = "java(user.getOtpSecret() != null)")
    fun toUserExportDto(user: User): UserExportDto
}
