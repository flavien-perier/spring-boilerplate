package io.flavien.demo.domain.tenant.model

data class DbDefinition(
    val jdbcUrl: String,
    val username: String,
    val password: String,
    val schema: String,
) {
    override fun toString(): String = "DbDefinition(jdbcUrl=$jdbcUrl, username=$username, password=***, schema=$schema)"
}
