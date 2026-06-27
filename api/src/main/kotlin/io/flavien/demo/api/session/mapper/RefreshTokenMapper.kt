package io.flavien.demo.api.session.mapper

import io.flavien.demo.api.generated.dto.RefreshTokenPropertiesDto
import io.flavien.demo.api.generated.dto.RefreshTokenPropertiesPageDto
import io.flavien.demo.domain.session.entity.RefreshToken
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.springframework.data.domain.Page

@Mapper(componentModel = "spring")
interface RefreshTokenMapper {
    fun toRefreshTokenPropertiesDto(refreshToken: RefreshToken): RefreshTokenPropertiesDto

    fun toRefreshTokenPropertiesDtoList(refreshTokens: List<RefreshToken>): List<RefreshTokenPropertiesDto>

    @Mapping(target = "propertySize", expression = "java(page.getSize())")
    fun toRefreshTokenPropertiesPageDto(page: Page<RefreshToken>): RefreshTokenPropertiesPageDto
}
