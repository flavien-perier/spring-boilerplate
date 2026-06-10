package io.flavien.demo.domain.session.model

/**
 * Single source of truth for the refresh token lifetime: used both for the
 * Redis TTL of [io.flavien.demo.domain.session.entity.RefreshToken] and for
 * the max-age of the web refresh token cookie set by the api module.
 */
const val REFRESH_TOKEN_TTL_SECONDS: Long = 3600L * 24 * 365
