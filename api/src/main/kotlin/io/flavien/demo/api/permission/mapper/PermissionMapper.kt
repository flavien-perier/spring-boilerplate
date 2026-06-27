package io.flavien.demo.api.permission.mapper

import io.flavien.demo.api.generated.dto.PermissionSettingDto
import io.flavien.demo.api.generated.dto.PermissionSettingPageDto
import io.flavien.demo.domain.permission.model.PermissionSetting
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.springframework.data.domain.Page

@Mapper(componentModel = "spring")
interface PermissionMapper {
    @Mapping(target = "permission", expression = "java(setting.getPermission().name())")
    fun toPermissionSettingDto(setting: PermissionSetting): PermissionSettingDto

    fun toPermissionSettingDtoList(settings: List<PermissionSetting>): List<PermissionSettingDto>

    @Mapping(target = "propertySize", expression = "java(page.getSize())")
    fun toPermissionSettingPageDto(page: Page<PermissionSetting>): PermissionSettingPageDto
}
