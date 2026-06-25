package io.flavien.demo.api.group.mapper

import io.flavien.demo.api.group.GroupTestFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.UUID

class GroupMapperTest {
    private val mapper = GroupMapperImpl()

    @Test
    fun `Should map Group to GroupDto`() {
        val parent = GroupTestFactory.initGroup(id = UUID.fromString("00000000-0000-7000-8000-000000000010"), name = "PARENT")
        val group =
            GroupTestFactory.initGroup(
                id = UUID.fromString("00000000-0000-7000-8000-000000000001"),
                name = "CHILD",
                parent = parent,
            )

        val result = mapper.toGroupDto(group)

        assertThat(result.id).isEqualTo("00000000-0000-7000-8000-000000000001")
        assertThat(result.name).isEqualTo("CHILD")
        assertThat(result.parentId).isEqualTo("00000000-0000-7000-8000-000000000010")
        assertThat(result.parentName).isEqualTo("PARENT")
    }

    @Test
    fun `Should map Group with null parent to GroupDto`() {
        val group = GroupTestFactory.initGroup(id = UUID.fromString("00000000-0000-7000-8000-000000000001"), name = "ROOT", parent = null)

        val result = mapper.toGroupDto(group)

        assertThat(result.id).isEqualTo("00000000-0000-7000-8000-000000000001")
        assertThat(result.name).isEqualTo("ROOT")
        assertThat(result.parentId).isNull()
        assertThat(result.parentName).isNull()
    }
}
