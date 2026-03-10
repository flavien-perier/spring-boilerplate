package io.flavien.demo.domain.testCore.util

import org.subethamail.wiser.Wiser
import org.subethamail.wiser.WiserMessage
import java.net.ServerSocket

object MailServerUtil {

    fun create(): Wiser {
        val serverSocket = ServerSocket(0)
        val port = serverSocket.localPort
        serverSocket.close()

        val smtp = Wiser.port(port)
        smtp.start()

        return smtp
    }

    fun getLastMail(smtp: Wiser, expectedNumberOfMails: Int): WiserMessage {
        for (i in 1..40) {
            Thread.sleep(50)
            val messages = smtp.messages
            if (messages.size == expectedNumberOfMails) {
                return messages.last()
            }
        }

        throw RuntimeException("Mail reception error")
    }
}