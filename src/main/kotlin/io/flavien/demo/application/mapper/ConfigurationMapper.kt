package io.flavien.demo.application.mapper

import io.flavien.demo.config.ApplicationProperties
import io.flavien.demo.dto.ConfigurationDto
import org.mapstruct.Mapper

@Mapper(componentModel="spring")
interface ConfigurationMapper {

    fun toConfigurationDto(applicationProperties: ApplicationProperties): ConfigurationDto
}