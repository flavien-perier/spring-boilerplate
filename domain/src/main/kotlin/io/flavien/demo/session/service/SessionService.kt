package io.flavien.demo.session.service

import io.flavien.demo.session.exception.BadPasswordException
import io.flavien.demo.session.exception.BadRefreshTokenException
import io.flavien.demo.session.exception.UserIsDisabledException
import io.flavien.demo.session.model.Session
import io.flavien.demo.user.exception.UserNotFoundException
import io.flavien.demo.user.service.UserService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class SessionService(
    private val refreshTokenService: RefreshTokenService,
    private val accessTokenService: AccessTokenService,
    private val userService: UserService,
    private val passwordService: PasswordService,
) {

    fun login(email: String, password: String, proofOfWork: String): Session {
        val user = userService.get(email) ?: throw UserNotFoundException(email)

        if (!user.enabled) {
            throw UserIsDisabledException(email)
        }

        if (!passwordService.testPassword(password, user.passwordSalt, user.password)) {
            logger.warn("Bad password for user $email")
            throw BadPasswordException()
        }

        if (user.proofOfWork != proofOfWork) {
            logger.warn("Bad proofOfWork for user $email")
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

    companion object {
        private var logger = LoggerFactory.getLogger(SessionService::class.java)
    }
}