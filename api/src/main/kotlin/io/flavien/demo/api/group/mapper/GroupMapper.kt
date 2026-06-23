package io.flavien.demo.api.group.mapper

import io.flavien.demo.api.generated.dto.GroupDto
import io.flavien.demo.domain.group.entity.Group
import org.mapstruct.Mapper
import org.mapstruct.Mapping

@Mapper(componentModel = "spring")
interface GroupMapper {
    @Mapping(target = "parentId", source = "parent.id")
    @Mapping(target = "parentName", source = "parent.name")
    fun toGroupDto(group: Group): GroupDto
}
