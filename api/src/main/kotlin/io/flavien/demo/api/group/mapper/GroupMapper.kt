package io.flavien.demo.api.group.mapper

import io.flavien.demo.api.generated.dto.GroupDto
import io.flavien.demo.api.generated.dto.GroupPageDto
import io.flavien.demo.domain.group.entity.Group
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.springframework.data.domain.Page

@Mapper(componentModel = "spring")
interface GroupMapper {
    @Mapping(target = "parentId", source = "parent.id")
    @Mapping(target = "parentName", source = "parent.name")
    fun toGroupDto(group: Group): GroupDto

    @Mapping(target = "propertySize", expression = "java(page.getSize())")
    fun toGroupPageDto(page: Page<Group>): GroupPageDto
}
