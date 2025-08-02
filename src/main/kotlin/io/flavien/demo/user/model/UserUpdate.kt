package io.flavien.demo.user.model

data class UserUpdate(
    var email: String? = null,
    var password: String? = null,
    var proofOfWork: String? = null,
    var role: UserRole? = null,
)