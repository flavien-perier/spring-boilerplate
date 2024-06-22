package io.flavien.demo.user.service

import io.flavien.demo.core.util.RandomUtil
import io.flavien.demo.session.service.AccessTokenService
import io.flavien.demo.session.service.RefreshTokenService
import io.flavien.demo.session.util.PasswordUtil
import io.flavien.demo.user.entity.User
import io.flavien.demo.user.exception.UserAlreadyExistsException
import io.flavien.demo.user.exception.UserNotFoundException
import io.flavien.demo.user.model.UserRole
import io.flavien.demo.user.model.UserUpdate
import io.flavien.demo.user.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
    private val userActivationService: UserActivationService,
    private val forgotPasswordService: ForgotPasswordService,
    private val accessTokenService: AccessTokenService,
    private val refreshTokenService: RefreshTokenService,
) {

    fun create(email: String, password: String): User {
        if (userRepository.existsByEmail(email)) {
            logger.info("User $email already exists")
            throw UserAlreadyExistsException(email)
        }

        val salt = RandomUtil.randomString(PASSWORD_SALT_LENGTH)

        val user = User(
            email,
            PasswordUtil.hashPassword(password, salt),
            salt,
            UserRole.USER,
            false,
        )

        val savedUser = userRepository.save(user)
        logger.info("User $email has been created")
        userActivationService.sendActivationToken(savedUser)

        return user
    }

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

    fun updatePassword(password: String, token: String) {
        val forgotPassword = forgotPasswordService.validate(token)

        val user = get(forgotPassword.userId)
        val salt = RandomUtil.randomString(PASSWORD_SALT_LENGTH)
        user.passwordSalt = salt
        user.password = PasswordUtil.hashPassword(password, salt)
        user.enabled = true
        userRepository.save(user)
    }

    fun disables(userId: Long) {
        val user = get(userId)
        user.enabled = false
        accessTokenService.deleteByUserId(user.id!!)
        refreshTokenService.deleteByUserId(user.id!!)
        userActivationService.deleteByUserId(user.id!!)
        forgotPasswordService.deleteByUserId(user.id!!)
        userRepository.save(user)
    }

    fun update(userId: Long, userUpdate: UserUpdate) = update(get(userId), userUpdate)

    fun update(email: String, userUpdate: UserUpdate) = update(get(email), userUpdate)

    private fun update(user: User, userUpdate: UserUpdate): User {
        if (userUpdate.email != null && user.email != userUpdate.email && userRepository.existsByEmail(userUpdate.email!!)) {
            logger.info("User ${userUpdate.email} already exists")
            throw UserAlreadyExistsException(userUpdate.email!!)
        }

        userUpdate.email?.let {
            user.email = it
        }

        userUpdate.password?.let {
            val salt = RandomUtil.randomString(PASSWORD_SALT_LENGTH)
            user.passwordSalt = salt
            user.password = PasswordUtil.hashPassword(it, salt)
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

    fun delete(userId: Long) = delete(get(userId))

    fun delete(email: String) = delete(get(email))

    fun find(
        query: String?,
        page: Int?,
        pageSize: Int?,
        sortColumn: String?,
        sortOrder: String?,
    ): Page<User> {
        val pageable = PageRequest.of(
            page ?: 0,
            pageSize ?: 10,
            Sort.by(Sort.Direction.fromString(sortOrder ?: "ASC"), sortColumn ?: "email"),
        )

        return userRepository.find(query ?: "", pageable)
    }

    private fun delete(user: User) {
        accessTokenService.deleteByUserId(user.id!!)
        refreshTokenService.deleteByUserId(user.id!!)
        userActivationService.deleteByUserId(user.id!!)
        forgotPasswordService.deleteByUserId(user.id!!)
        userRepository.delete(user)
        logger.info("User ${user.email} has been deleted")
    }

    companion object {
        private var logger = LoggerFactory.getLogger(UserService::class.java)

        private const val PASSWORD_SALT_LENGTH = 10L
    }
}