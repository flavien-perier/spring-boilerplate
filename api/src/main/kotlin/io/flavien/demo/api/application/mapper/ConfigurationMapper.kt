package io.flavien.demo.api.application.mapper

import io.flavien.demo.domain.config.ApplicationProperties
import io.flavien.demo.api.dto.ConfigurationDto
import org.mapstruct.Mapper

@Mapper(componentModel="spring")
interface ConfigurationMapper {

    fun toConfigurationDto(applicationProperties: ApplicationProperties): ConfigurationDto
}