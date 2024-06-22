package io.flavien.demo.session.util

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class PasswordUtilTest {

    @ParameterizedTest
    @CsvSource(
        "perier@flavien.io,test,b5ec7332bbbdfa773092a997603f8a73e1aee4e318bcb934c9fa5edd73ef42cea1ed58d2ef7f90acbb2577764c55e8b63614a90b7cdcb5c8a9af4df4ddd4e052",
        "perier@flavien.io,123,f26e598af63478651101364243c59916593d49662e7065249116ed823a40416f1e1b3182322ffbad299f09d86e1d086e83a730660b3072370110246adcd02f8f",
        "flavien.perier@pm.me,123,2f084ce19cf2152304b71e34b80664fdf0bcf8b8cba0ff31f239f924d4796381121a88c4395846d634ab36ff12c389e4250deabe09e183f618c257c48f6f7751",
    )
    fun `should have the same hash`(password: String, salt: String, hash: String) {
        assertThat(PasswordUtil.hashPassword(password, salt))
            .isEqualTo(hash)
    }
}