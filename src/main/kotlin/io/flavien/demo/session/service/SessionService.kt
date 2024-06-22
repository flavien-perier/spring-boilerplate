package io.flavien.demo.session.service

import io.flavien.demo.session.exception.BadPasswordException
import io.flavien.demo.session.exception.BadRefreshTokenException
import io.flavien.demo.session.exception.UserIsDisabledException
import io.flavien.demo.session.model.Session
import io.flavien.demo.session.util.PasswordUtil
import io.flavien.demo.user.exception.UserNotFoundException
import io.flavien.demo.user.service.UserService
import org.springframework.stereotype.Service

@Service
class SessionService(
    private val refreshTokenService: RefreshTokenService,
    private val accessTokenService: AccessTokenService,
    private val userService: UserService,
) {

    fun login(email: String, password: String): Session {
        val user = userService.get(email) ?: throw UserNotFoundException(email)

        if (!user.enabled) {
            throw UserIsDisabledException(email)
        }

        if (!PasswordUtil.testPassword(password, user.passwordSalt, user.password)) {
            throw BadPasswordException()
        }

        val refreshToken = refreshTokenService.create(user.id!!, user.role)
        val accessToken = accessTokenService.create(refreshToken)

        return Session(refreshToken, accessToken)
    }

    fun renew(userEmail: String, refreshToken: String): Session {
        val refreshTokenObject = refreshTokenService.get(refreshToken)
        val user = userService.get(userEmail)

        if (refreshTokenObject.userId != user.id) {
            throw BadRefreshTokenException()
        }

        val accessToken = accessTokenService.create(refreshTokenObject)

        return Session(null, accessToken)
    }
}