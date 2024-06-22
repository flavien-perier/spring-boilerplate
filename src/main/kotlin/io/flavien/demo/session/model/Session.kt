package io.flavien.demo.session.model

import io.flavien.demo.session.entity.AccessToken
import io.flavien.demo.session.entity.RefreshToken

data class Session(
    val refreshToken: RefreshToken?,
    val accessToken: AccessToken,
)