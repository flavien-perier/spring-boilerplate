package io.flavien.demo.api.session.mapper

import io.flavien.demo.api.dto.RefreshTokenPropertiesDto
import io.flavien.demo.domain.session.entity.RefreshToken
import org.mapstruct.Mapper

@Mapper(componentModel="spring")
interface RefreshTokenMapper {

    fun toRefreshTokenPropertiesDto(refreshToken: RefreshToken): RefreshTokenPropertiesDto

    fun toRefreshTokenPropertiesDtoList(refreshTokens: List<RefreshToken>): List<RefreshTokenPropertiesDto>
}