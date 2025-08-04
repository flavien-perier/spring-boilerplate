package io.flavien.demo.application

import io.flavien.demo.api.ApplicationApi
import io.flavien.demo.application.mapper.ConfigurationMapper
import io.flavien.demo.config.ApplicationProperties
import io.flavien.demo.dto.ConfigurationDto
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller

@Controller
class ApplicationController(
    private val configurationMapper: ConfigurationMapper,
    private val applicationProperties: ApplicationProperties,
): ApplicationApi {

    override fun getConf(): ResponseEntity<ConfigurationDto> {
        return ResponseEntity(
            configurationMapper.toConfigurationDto(applicationProperties),
            HttpStatus.OK,
        )
    }
}