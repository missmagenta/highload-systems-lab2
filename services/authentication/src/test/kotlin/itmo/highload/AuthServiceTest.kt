package itmo.highload

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.mockk.mockk
import itmo.highload.service.UserService
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import itmo.highload.dto.RegisterDto
import itmo.highload.model.User
import itmo.highload.repository.UserRepository
import itmo.highload.security.Role
import itmo.highload.security.jwt.JwtUtils
import itmo.highload.service.AuthService
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.crypto.password.PasswordEncoder
import reactor.core.publisher.Mono
import java.time.LocalDate

class AuthServiceTest {

    private val jwtProvider: JwtUtils = mockk()
    private val encoder: PasswordEncoder = mockk()
    val userService = mockk<UserService>(relaxed = true)
    private val authService = AuthService(jwtProvider, userService, encoder)

    @Test
    fun `should return JWT response on successful login`() {
        val login = "testUser"
        val password = "testPassword"
        val encodedPassword = "encodedPassword"
        val user = User(
            id = "1",
            login = login,
            password = encodedPassword,
            role = Role.USER,
            registrationDate = LocalDate.now()
        )
        val accessToken = "generatedAccessToken"

        every { userService.getByLogin(login) } returns Mono.just(user)

        every { encoder.matches(password, encodedPassword) } returns true

        every { jwtProvider.generateAccessToken(login, Role.USER, "1") } returns accessToken

        val result = authService.login(login, password).block()

        assertNotNull(result)
        assertEquals(accessToken, result?.accessToken)
        assertEquals(Role.USER, result?.role)

        verify { userService.getByLogin(login) }
        verify { encoder.matches(password, encodedPassword) }
        verify { jwtProvider.generateAccessToken(login, Role.USER, "1") }
    }

    @Test
    fun `should throw BadCredentialsException on wrong password`() {
        val login = "testUser"
        val password = "wrongPassword"
        val encodedPassword = "encodedPassword"
        val user = User(
            id = "1",
            login = login,
            password = encodedPassword,
            role = Role.USER,
            registrationDate = LocalDate.now()
        )

        every { userService.getByLogin(login) } returns Mono.just(user)

        every { encoder.matches(password, encodedPassword) } returns false

        val exception = assertThrows<BadCredentialsException> {
            authService.login(login, password).block()
        }
        assertEquals("Wrong password", exception.message)
    }

    @Test
    fun `should throw NoSuchElementException for nonexistent user`() {
        val login = "nonexistentUser"

        every { userService.getByLogin(login) } returns Mono.error(NoSuchElementException("User not found"))

        val exception = assertThrows<NoSuchElementException> {
            authService.login(login, "anyPassword").block()
        }
        assertEquals("User not found", exception.message)
    }

    @Test
    fun `should return true when user exists`() {
        val login = "existingUser"

        every { userService.checkIfExists(login) } returns Mono.just(true)

        val result = authService.checkIfUserExists(login).block()

        assertTrue(result!!)
        verify { userService.checkIfExists(login) }
    }

    @Test
    fun `should successfully register a user`() {
        val request = RegisterDto(login = "newUser", password = "password123", role = Role.USER)
        val user = User(
            id = "1",
            login = "newUser",
            password = "encodedPassword",
            role = Role.USER,
            registrationDate = LocalDate.now()
        )

        every { userService.addUser(request) } returns Mono.just(user)

        val result = authService.register(request).block()

        assertNotNull(result)
        assertEquals(user, result)
        verify { userService.addUser(request) }
    }

}