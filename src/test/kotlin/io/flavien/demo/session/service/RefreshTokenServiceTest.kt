package io.flavien.demo.session.service

import io.flavien.demo.session.repository.RefreshTokenRepository
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class RefreshTokenServiceTest {

    @InjectMocks
    var refreshTokenService: RefreshTokenService? = null

    @Mock
    var refreshTokenRepository: RefreshTokenRepository? = null

    // TODO: Implement tests
    @Test
    fun `Should create an refresh token`() {
        // Given

        // When

        // Then
    }

    // TODO: Implement tests
    @Test
    fun `Should return an refresh token according to its id`() {
        // Given

        // When

        // Then
    }

    // TODO: Implement tests
    @Test
    fun `Should fail to return an refresh token according to its id (Refresh token does not exist)`() {
        // Given

        // When

        // Then
    }

    // TODO: Implement tests
    @Test
    fun `Should return an refresh token according to its uuid`() {
        // Given

        // When

        // Then
    }

    // TODO: Implement tests
    @Test
    fun `Should fail to return an refresh token according to its uuid (Refresh token does not exist)`() {
        // Given

        // When

        // Then
    }

    // TODO: Implement tests
    @Test
    fun `Should return a list of refresh tokens based on an userId`() {
        // Given

        // When

        // Then
    }

    // TODO: Implement tests
    @Test
    fun `Should return an empty list of refresh tokens based on an userId (No userId matches)`() {
        // Given

        // When

        // Then
    }

    // TODO: Implement tests
    @Test
    fun `Should return that the refresh token exists according to its id`() {
        // Given

        // When

        // Then
    }

    // TODO: Implement tests
    @Test
    fun `Should return that the refresh token does not exist according to its id`() {
        // Given

        // When

        // Then
    }

    // TODO: Implement tests
    @Test
    fun `Should delete a refresh token according to its id`() {
        // Given

        // When

        // Then
    }

    // TODO: Implement tests
    @Test
    fun `Should delete a refresh token according to its uuid`() {
        // Given

        // When

        // Then
    }

    // TODO: Implement tests
    @Test
    fun `Should delete a refresh token according to its userId`() {
        // Given

        // When

        // Then
    }
}