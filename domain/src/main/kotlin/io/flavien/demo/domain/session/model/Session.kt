package io.flavien.demo.domain.session.model

import io.flavien.demo.domain.session.entity.AccessToken
import io.flavien.demo.domain.session.entity.RefreshToken


data class Session(
    val refreshToken: RefreshToken?,
    val accessToken: AccessToken,
)