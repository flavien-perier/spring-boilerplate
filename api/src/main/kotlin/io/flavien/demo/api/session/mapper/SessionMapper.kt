package io.flavien.demo.api.session.mapper

import io.flavien.demo.api.dto.SessionDto
import io.flavien.demo.api.dto.SessionWebDto
import io.flavien.demo.domain.session.model.Session
import org.mapstruct.Mapper
import org.mapstruct.Mapping

@Mapper(componentModel="spring")
interface SessionMapper {

    @Mapping(source = "refreshToken.id", target = "refreshToken")
    @Mapping(source = "accessToken.id", target = "accessToken")
    fun toSessionDto(session: Session): SessionDto


    @Mapping(source = "accessToken.id", target = "accessToken")
    fun toSessionWebDto(session: Session): SessionWebDto
}