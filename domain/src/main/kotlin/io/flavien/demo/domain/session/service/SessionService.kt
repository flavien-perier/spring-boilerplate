package io.flavien.demo.domain.session.service

import io.flavien.demo.domain.session.exception.BadPasswordException
import io.flavien.demo.domain.session.exception.BadRefreshTokenException
import io.flavien.demo.domain.session.exception.InvalidOtpException
import io.flavien.demo.domain.session.exception.OtpRequiredException
import io.flavien.demo.domain.session.exception.UserIsDisabledException
import io.flavien.demo.domain.session.model.Session
import io.flavien.demo.domain.user.repository.UserRepository
import io.flavien.demo.domain.user.service.UserService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.OffsetDateTime

@Service
class SessionService(
    private val refreshTokenService: RefreshTokenService,
    private val accessTokenService: AccessTokenService,
    private val userService: UserService,
    private val userRepository: UserRepository,
    private val passwordService: PasswordService,
    private val otpService: OtpService,
) {
    fun login(
        email: String,
        password: String,
        proofOfWork: String,
        otp: String? = null,
    ): Session {
        val user = userService.get(email)

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

        val otpSecret = user.otpSecret
        if (otpSecret != null) {
            if (otp.isNullOrBlank()) {
                throw OtpRequiredException()
            }
            if (!otpService.validateTOTP(otpSecret, otp)) {
                throw InvalidOtpException()
            }
        }

        user.lastLogin = OffsetDateTime.now()
        userRepository.save(user)
        val refreshToken = refreshTokenService.create(user.id!!, user.role)
        val accessToken = accessTokenService.create(refreshToken)
        return Session(refreshToken, accessToken)
    }

    fun renew(
        userEmail: String,
        refreshToken: String,
    ): Session {
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
