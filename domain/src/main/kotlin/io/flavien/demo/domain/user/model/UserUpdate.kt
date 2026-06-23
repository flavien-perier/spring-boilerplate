package io.flavien.demo.domain.user.model

data class UserUpdate(
    var email: String? = null,
    var password: String? = null,
    var proofOfWork: String? = null,
    var enabled: Boolean? = null,
)
