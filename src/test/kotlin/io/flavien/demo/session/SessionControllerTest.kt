package io.flavien.demo.session

import io.flavien.demo.session.mapper.RefreshTokenMapper
import io.flavien.demo.session.mapper.SessionMapper
import io.flavien.demo.session.service.RefreshTokenService
import io.flavien.demo.session.service.SessionService
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class SessionControllerTest {

    @InjectMocks
    var sessionController: SessionController? = null

    @Mock
    var sessionService: SessionService? = null

    @Mock
    var refreshTokenService: RefreshTokenService? = null

    @Mock
    var sessionMapper: SessionMapper? = null

    @Mock
    var refreshTokenMapper: RefreshTokenMapper? = null

    // TODO: Implement tests
    @Test
    fun `Test login`() {
        // Given

        // When

        // Then
    }

    // TODO: Implement tests
    @Test
    fun `Test logout`() {
        // Given

        // When

        // Then
    }

    // TODO: Implement tests
    @Test
    fun `Test findSessions`() {
        // Given

        // When

        // Then
    }

    // TODO: Implement tests
    @Test
    fun `Test renewSession`() {
        // Given

        // When

        // Then
    }

    // TODO: Implement tests
    @Test
    fun `Test deleteSession`() {
        // Given

        // When

        // Then
    }
}