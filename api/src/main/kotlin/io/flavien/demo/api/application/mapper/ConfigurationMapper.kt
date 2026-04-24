package io.flavien.demo.api.application.mapper

import io.flavien.demo.api.generated.dto.ConfigurationDto
import io.flavien.demo.domain.config.ApplicationProperties
import org.mapstruct.Mapper

@Mapper(componentModel = "spring")
interface ConfigurationMapper {
    fun toConfigurationDto(applicationProperties: ApplicationProperties): ConfigurationDto
}
