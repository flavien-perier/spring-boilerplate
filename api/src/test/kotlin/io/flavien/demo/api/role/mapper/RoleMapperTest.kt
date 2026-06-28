package io.flavien.demo.api.role.mapper

import io.flavien.demo.api.role.RoleTestFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.UUID

class RoleMapperTest {
    private val mapper = RoleMapperImpl()

    @Test
    fun `Should map Role to RoleDto`() {
        val parent = RoleTestFactory.initRole(id = UUID.fromString("00000000-0000-7000-8000-000000000010"), name = "PARENT")
        val role =
            RoleTestFactory.initRole(
                id = UUID.fromString("00000000-0000-7000-8000-000000000001"),
                name = "CHILD",
                parent = parent,
            )

        val result = mapper.toRoleDto(role)

        assertThat(result.id).isEqualTo("00000000-0000-7000-8000-000000000001")
        assertThat(result.name).isEqualTo("CHILD")
        assertThat(result.parentId).isEqualTo("00000000-0000-7000-8000-000000000010")
        assertThat(result.parentName).isEqualTo("PARENT")
    }

    @Test
    fun `Should map Role with null parent to RoleDto`() {
        val role = RoleTestFactory.initRole(id = UUID.fromString("00000000-0000-7000-8000-000000000001"), name = "ROOT", parent = null)

        val result = mapper.toRoleDto(role)

        assertThat(result.id).isEqualTo("00000000-0000-7000-8000-000000000001")
        assertThat(result.name).isEqualTo("ROOT")
        assertThat(result.parentId).isNull()
        assertThat(result.parentName).isNull()
    }
}
