package io.flavien.demo.domain.user.service

import io.flavien.demo.domain.session.entity.OtpPending
import io.flavien.demo.domain.session.exception.InvalidOtpException
import io.flavien.demo.domain.session.exception.OtpAlreadyConfiguredException
import io.flavien.demo.domain.session.exception.OtpNotPendingException
import io.flavien.demo.domain.session.repository.OtpPendingRepository
import io.flavien.demo.domain.session.service.AccessTokenService
import io.flavien.demo.domain.session.service.OtpService
import io.flavien.demo.domain.session.service.PasswordService
import io.flavien.demo.domain.session.service.RefreshTokenService
import io.flavien.demo.domain.shared.util.SECURE_RANDOM
import io.flavien.demo.domain.user.entity.User
import io.flavien.demo.domain.user.exception.UserAlreadyExistsException
import io.flavien.demo.domain.user.exception.UserNotFoundException
import io.flavien.demo.domain.user.model.UserRole
import io.flavien.demo.domain.user.model.UserUpdate
import io.flavien.demo.domain.user.repository.UserRepository
import io.flavien.demo.library.common.RandomUtil
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime

@Service
class UserService(
    private val userRepository: UserRepository,
    private val userActivationService: UserActivationService,
    private val forgotPasswordService: ForgotPasswordService,
    private val accessTokenService: AccessTokenService,
    private val refreshTokenService: RefreshTokenService,
    private val passwordService: PasswordService,
    private val otpService: OtpService,
    private val otpPendingRepository: OtpPendingRepository,
) {
    @Transactional
    fun create(
        email: String,
        password: String,
        proofOfWork: String,
    ): User {
        if (userRepository.existsByEmail(email)) {
            log.info("User $email already exists")
            throw UserAlreadyExistsException(email)
        }

        val salt = RandomUtil.randomString(PASSWORD_SALT_LENGTH, SECURE_RANDOM)
        val user =
            User(
                email = email,
                password = passwordService.hashPassword(password, salt),
                proofOfWork = proofOfWork,
                passwordSalt = salt,
                role = UserRole.USER,
                enabled = false,
                lastLogin = OffsetDateTime.now(),
            )

        val savedUser = userRepository.save(user)
        log.info("User $email has been created")
        userActivationService.sendActivationToken(savedUser)

        return savedUser
    }

    @Transactional
    fun activate(token: String) {
        val userAction = userActivationService.validate(token)

        val user = get(userAction.userId)
        user.enabled = true
        userRepository.save(user)
    }

    fun sendForgotPassword(email: String) {
        val user = get(email)

        forgotPasswordService.sendForgotPasswordToken(user)
    }

    @Transactional
    fun updatePassword(
        password: String,
        proofOfWork: String,
        token: String,
    ) {
        val forgotPassword = forgotPasswordService.validate(token)

        val user = get(forgotPassword.userId)
        val salt = RandomUtil.randomString(PASSWORD_SALT_LENGTH, SECURE_RANDOM)
        user.passwordSalt = salt
        user.password = passwordService.hashPassword(password, salt)
        user.proofOfWork = proofOfWork
        user.enabled = true
        user.otpSecret = null
        otpPendingRepository.deleteById(user.id.toString())
        userRepository.save(user)
    }

    @Transactional
    fun disable(userId: Long) {
        val user = get(userId)
        val id = user.id!!
        user.enabled = false
        userRepository.save(user)
        accessTokenService.deleteByUserId(id)
        refreshTokenService.deleteByUserId(id)
        userActivationService.deleteByUserId(id)
        forgotPasswordService.deleteByUserId(id)
    }

    @Transactional
    fun update(
        userId: Long,
        userUpdate: UserUpdate,
    ) = update(get(userId), userUpdate)

    @Transactional
    fun update(
        email: String,
        userUpdate: UserUpdate,
    ) = update(get(email), userUpdate)

    private fun update(
        user: User,
        userUpdate: UserUpdate,
    ): User {
        if (userUpdate.email != null && user.email != userUpdate.email && userRepository.existsByEmail(userUpdate.email!!)) {
            log.info("User ${userUpdate.email} already exists")
            throw UserAlreadyExistsException(userUpdate.email!!)
        }

        userUpdate.email?.let {
            user.email = it
        }

        userUpdate.password?.let {
            val salt = RandomUtil.randomString(PASSWORD_SALT_LENGTH, SECURE_RANDOM)
            user.passwordSalt = salt
            user.password = passwordService.hashPassword(it, salt)
        }

        userUpdate.proofOfWork?.let {
            user.proofOfWork = it
        }

        userUpdate.role?.let {
            user.role = it
        }

        userRepository.save(user)

        return user
    }

    fun exists(userId: Long) = userRepository.existsById(userId)

    fun exists(email: String) = userRepository.existsByEmail(email)

    fun get(userId: Long) = userRepository.getUserById(userId).orElseThrow { UserNotFoundException(userId) }

    fun get(email: String) = userRepository.getByEmail(email).orElseThrow { UserNotFoundException(email) }

    @Transactional
    fun delete(userId: Long) = delete(get(userId))

    @Transactional
    fun delete(email: String) = delete(get(email))

    fun find(
        query: String?,
        page: Int?,
        pageSize: Int?,
        sortColumn: String?,
        sortOrder: String?,
    ): Page<User> {
        val pageable =
            PageRequest.of(
                page ?: 0,
                pageSize ?: 10,
                Sort.by(Sort.Direction.fromString(sortOrder ?: "ASC"), sortColumn ?: "email"),
            )

        return userRepository.find(query ?: "", pageable)
    }

    fun setupOtp(userId: Long): String {
        val user = get(userId)

        if (user.otpSecret != null) {
            throw OtpAlreadyConfiguredException()
        }

        if (otpPendingRepository.existsById(userId.toString())) {
            throw OtpAlreadyConfiguredException()
        }

        val secret = otpService.generateSecret()
        otpPendingRepository.save(OtpPending(userId.toString(), secret))

        return "otpauth://totp/Demo:${user.email}?secret=$secret&issuer=Demo&algorithm=SHA1&digits=6&period=30"
    }

    @Transactional
    fun confirmOtp(
        userId: Long,
        otp: String,
    ) {
        val pending = otpPendingRepository.findById(userId.toString()).orElseThrow { OtpNotPendingException() }

        if (!otpService.validateTOTP(pending.secret, otp)) {
            throw InvalidOtpException()
        }

        val user = get(userId)
        user.otpSecret = pending.secret
        userRepository.save(user)
        otpPendingRepository.deleteById(userId.toString())
    }

    @Transactional
    fun disableOtp(userId: Long) {
        val user = get(userId)
        if (user.otpSecret != null) {
            user.otpSecret = null
            userRepository.save(user)
        }
        otpPendingRepository.deleteById(userId.toString())
    }

    private fun delete(user: User) {
        val userId = user.id!!
        userRepository.delete(user)
        accessTokenService.deleteByUserId(userId)
        refreshTokenService.deleteByUserId(userId)
        userActivationService.deleteByUserId(userId)
        forgotPasswordService.deleteByUserId(userId)
        log.info("User ${user.email} has been deleted")
    }

    companion object {
        private val log = LoggerFactory.getLogger(UserService::class.java)
        private const val PASSWORD_SALT_LENGTH = 10
    }
}
