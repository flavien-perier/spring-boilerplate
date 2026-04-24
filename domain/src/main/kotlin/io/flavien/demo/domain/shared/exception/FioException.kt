package io.flavien.demo.domain.shared.exception

abstract class FioException(
    message: String,
) : RuntimeException(message)
