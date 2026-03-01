package io.flavien.demo.session.util

import io.flavien.demo.session.exception.ContextException
import io.flavien.demo.user.model.UserRole
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes


object ContextUtil {

    private const val KEY_USER_ID = "FLAVIEN_IO_USER_ID"
    private const val KEY_USER_ROLE = "FLAVIEN_IO_USER_ROLE"
    private const val KEY_SESSION_ID = "FLAVIEN_IO_SESSION_ID"

    var userId: Long
        get() {
            val request = getRequest() ?: throw ContextException()
            return request.getAttribute(KEY_USER_ID) as Long
        }
        set(value) {
            val request = getRequest() ?: throw ContextException()
            request.setAttribute(KEY_USER_ID, value)
        }

    var userRole: UserRole
        get() {
            val request = getRequest() ?: throw ContextException()
            return request.getAttribute(KEY_USER_ROLE) as UserRole
        }
        set(value) {
            val request = getRequest() ?: throw ContextException()
            request.setAttribute(KEY_USER_ROLE, value)
        }

    var refreshTokenId: String
        get() {
            val request = getRequest() ?: throw ContextException()
            return request.getAttribute(KEY_SESSION_ID) as String
        }
        set(value) {
            val request = getRequest() ?: throw ContextException()
            request.setAttribute(KEY_SESSION_ID, value)
        }

    private fun getRequest() = (RequestContextHolder.getRequestAttributes() as ServletRequestAttributes?)?.request
}