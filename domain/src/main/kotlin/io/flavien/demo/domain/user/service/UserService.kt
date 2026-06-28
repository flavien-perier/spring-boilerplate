package io.flavien.demo.domain.user.service

import io.flavien.demo.domain.permission.service.PermissionService
import io.flavien.demo.domain.role.service.RoleService
import io.flavien.demo.domain.session.entity.OtpPending
import io.flavien.demo.domain.session.exception.InvalidOtpException
import io.flavien.demo.domain.session.exception.OtpAlreadyConfiguredException
import io.flavien.demo.domain.session.exception.OtpNotPendingException
import io.flavien.demo.domain.session.repository.OtpPendingRepository
import io.flavien.demo.domain.session.service.AccessTokenService
import io.flavien.demo.domain.session.service.OtpService
import io.flavien.demo.domain.session.service.PasswordService
import io.flavien.demo.domain.session.service.RefreshTokenService
import io.flavien.demo.domain.shared.util.PageableUtil
import io.flavien.demo.domain.shared.util.SECURE_RANDOM
import io.flavien.demo.domain.user.entity.User
import io.flavien.demo.domain.user.exception.UserAlreadyExistsException
import io.flavien.demo.domain.user.exception.UserNotFoundException
import io.flavien.demo.domain.user.model.UserUpdate
import io.flavien.demo.domain.user.repository.UserRepository
import io.flavien.demo.library.common.RandomUtil
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime
import java.util.UUID

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
    private val roleService: RoleService,
    private val permissionService: PermissionService,
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
                enabled = false,
                lastLogin = OffsetDateTime.now(),
            )

        val savedUser = userRepository.save(user)
        roleService.assignDefaultRole(savedUser)
        log.info("User $email has been created")
        userActivationService.sendActivationToken(savedUser)

        return savedUser
    }

    @Transactional
    fun activate(token: String) {
        val userAction = userActivationService.validate(token)

        val user = getById(UUID.fromString(userAction.userId))
        user.enabled = true
        userRepository.save(user)
    }

    fun sendForgotPassword(email: String) {
        val user = getByEmail(email)

        forgotPasswordService.sendForgotPasswordToken(user)
    }

    @Transactional
    fun updatePassword(
        password: String,
        proofOfWork: String,
        token: String,
    ) {
        val forgotPassword = forgotPasswordService.validate(token)

        val user = getById(UUID.fromString(forgotPassword.userId))
        val salt = RandomUtil.randomString(PASSWORD_SALT_LENGTH, SECURE_RANDOM)
        user.passwordSalt = salt
        user.password = passwordService.hashPassword(password, salt)
        user.proofOfWork = proofOfWork
        user.enabled = true
        user.otpSecret = null
        otpPendingRepository.deleteById(user.id!!.toString())
        userRepository.save(user)
    }

    @Transactional
    fun disable(userId: UUID) {
        val user = getById(userId)
        val id = user.id!!
        user.enabled = false
        userRepository.save(user)
        accessTokenService.deleteByUserId(id)
        refreshTokenService.deleteByUserId(id)
        userActivationService.deleteByUserId(id)
        forgotPasswordService.deleteByUserId(id)
    }

    @Transactional
    fun updateById(
        userId: UUID,
        userUpdate: UserUpdate,
    ) = update(getById(userId), userUpdate)

    @Transactional
    fun updateByEmail(
        email: String,
        userUpdate: UserUpdate,
    ) = update(getByEmail(email), userUpdate)

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

        userUpdate.enabled?.let {
            user.enabled = it
        }

        userRepository.save(user)

        return user
    }

    fun getById(userId: UUID) = userRepository.getUserById(userId).orElseThrow { UserNotFoundException("User id $userId not found") }

    fun getByEmail(email: String) = userRepository.getByEmail(email).orElseThrow { UserNotFoundException("User $email not found") }

    @Transactional
    fun deleteById(userId: UUID) = delete(getById(userId))

    @Transactional
    fun deleteByEmail(email: String) = delete(getByEmail(email))

    fun find(
        query: String?,
        page: Int?,
        pageSize: Int?,
        sortColumn: String?,
        sortOrder: String?,
    ): Page<User> {
        val pageable = PageableUtil.toPageable(page, pageSize, sortColumn, sortOrder, "email")

        return userRepository.find(query ?: "", pageable)
    }

    fun setupOtp(userId: UUID): String {
        val user = getById(userId)

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
        userId: UUID,
        otp: String,
    ) {
        val pending = otpPendingRepository.findById(userId.toString()).orElseThrow { OtpNotPendingException() }

        if (!otpService.validateTOTP(pending.secret, otp)) {
            throw InvalidOtpException()
        }

        val user = getById(userId)
        user.otpSecret = pending.secret
        userRepository.save(user)
        otpPendingRepository.deleteById(userId.toString())
    }

    @Transactional
    fun disableOtp(userId: UUID) {
        val user = getById(userId)
        if (user.otpSecret != null) {
            user.otpSecret = null
            userRepository.save(user)
        }
        otpPendingRepository.deleteById(userId.toString())
    }

    private fun delete(user: User) {
        val userId = user.id!!
        roleService.deleteUserRoles(userId)
        permissionService.deleteUserPermissions(userId)
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
