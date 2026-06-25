package io.flavien.demo.api.session.util

import io.flavien.demo.domain.session.exception.ContextException
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import java.util.UUID

object ContextUtil {
    private const val KEY_USER_ID = "FLAVIEN_IO_USER_ID"
    private const val KEY_SESSION_ID = "FLAVIEN_IO_SESSION_ID"

    var userId: UUID
        get() {
            val request = getRequest() ?: throw ContextException()
            val str = request.getAttribute(KEY_USER_ID) as? String ?: throw ContextException()
            return UUID.fromString(str)
        }
        set(value) {
            val request = getRequest() ?: throw ContextException()
            request.setAttribute(KEY_USER_ID, value.toString())
        }

    var refreshTokenId: String
        get() {
            val request = getRequest() ?: throw ContextException()
            return request.getAttribute(KEY_SESSION_ID) as? String ?: throw ContextException()
        }
        set(value) {
            val request = getRequest() ?: throw ContextException()
            request.setAttribute(KEY_SESSION_ID, value)
        }

    private fun getRequest() = (RequestContextHolder.getRequestAttributes() as ServletRequestAttributes?)?.request
}
