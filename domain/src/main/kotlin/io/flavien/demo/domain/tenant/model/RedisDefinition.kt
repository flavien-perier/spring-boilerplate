package io.flavien.demo.domain.tenant.model

data class RedisDefinition(
    val host: String,
    val port: Int = 6379,
    val password: String? = null,
    val database: Int = 0,
) {
    override fun toString(): String =
        "RedisDefinition(host=$host, port=$port, password=${if (password != null) "***" else "null"}, database=$database)"
}
