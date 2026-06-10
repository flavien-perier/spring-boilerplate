package io.flavien.demo.domain.tenant.model

data class SmtpDefinition(
    val host: String,
    val port: Int = 25,
    val username: String = "",
    val password: String = "",
    val auth: Boolean = false,
    val starttls: Boolean = false,
    val accountCreator: String,
    val domainLinks: String,
) {
    override fun toString(): String =
        "SmtpDefinition(host=$host, port=$port, username=$username, password=***, auth=$auth, starttls=$starttls, accountCreator=$accountCreator, domainLinks=$domainLinks)"
}
