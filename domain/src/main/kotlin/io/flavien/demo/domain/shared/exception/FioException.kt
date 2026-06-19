package io.flavien.demo.domain.shared.exception

abstract class FioException(
    message: String,
    val errorCode: String,
) : RuntimeException(message)
