package io.flavien.demo.session.mapper

import io.flavien.demo.dto.RefreshTokenPropertiesDto
import io.flavien.demo.session.entity.RefreshToken
import org.mapstruct.Mapper

@Mapper(componentModel="spring")
interface RefreshTokenMapper {

    fun toRefreshTokenPropertiesDto(refreshToken: RefreshToken): RefreshTokenPropertiesDto

    fun toRefreshTokenPropertiesDtoList(refreshTokens: List<RefreshToken>): List<RefreshTokenPropertiesDto>
}