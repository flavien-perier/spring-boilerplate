package io.flavien.demo.api.group.mapper

import io.flavien.demo.api.group.GroupTestFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class GroupMapperTest {
    private val mapper = GroupMapperImpl()

    @Test
    fun `Should map Group to GroupDto`() {
        val parent = GroupTestFactory.initGroup(id = 10L, name = "PARENT")
        val group = GroupTestFactory.initGroup(id = 1L, name = "CHILD", parent = parent)

        val result = mapper.toGroupDto(group)

        assertThat(result.id).isEqualTo(1L)
        assertThat(result.name).isEqualTo("CHILD")
        assertThat(result.parentId).isEqualTo(10L)
        assertThat(result.parentName).isEqualTo("PARENT")
    }

    @Test
    fun `Should map Group with null parent to GroupDto`() {
        val group = GroupTestFactory.initGroup(id = 1L, name = "ROOT", parent = null)

        val result = mapper.toGroupDto(group)

        assertThat(result.id).isEqualTo(1L)
        assertThat(result.name).isEqualTo("ROOT")
        assertThat(result.parentId).isNull()
        assertThat(result.parentName).isNull()
    }
}
