package io.flavien.demo.api.role.mapper

import io.flavien.demo.api.generated.dto.RoleDto
import io.flavien.demo.api.generated.dto.RolePageDto
import io.flavien.demo.domain.role.entity.Role
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.springframework.data.domain.Page

@Mapper(componentModel = "spring")
interface RoleMapper {
    @Mapping(target = "parentId", source = "parent.id")
    @Mapping(target = "parentName", source = "parent.name")
    fun toRoleDto(role: Role): RoleDto

    @Mapping(target = "propertySize", expression = "java(page.getSize())")
    fun toRolePageDto(page: Page<Role>): RolePageDto
}
