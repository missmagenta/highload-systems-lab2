package itmo.highload

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import itmo.highload.dto.RegisterDto
import itmo.highload.model.User
import itmo.highload.repository.UserRepository
import itmo.highload.security.Role
import itmo.highload.security.jwt.JwtUtils
import itmo.highload.service.UserService
import itmo.highload.service.AuthService
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.security.crypto.password.PasswordEncoder
import reactor.core.publisher.Mono
import java.time.LocalDate

class UsersServiceTest {
    private val passwordEncoder: PasswordEncoder = mockk()
    private val userRepository: UserRepository = mockk()
    private val userService = UserService(userRepository, passwordEncoder)


    private val jwtProvider: JwtUtils = mockk()
    private val encoder: PasswordEncoder = mockk()
    private val authService = AuthService(jwtProvider, userService, encoder)

    @Test
    fun `should return user when found by login`() {
        val users = User(
            id = "1",
            login = "manager",
            password = "123",
            role = Role.OWNER,
            registrationDate = LocalDate.now()
        )

        every { userRepository.findByLogin("manager") } returns Mono.just(users)

        val result = userService.getByLogin("manager").block()

        assertEquals(users, result)
        verify { userRepository.findByLogin("manager") }
    }

    @Test
    fun `should throw NoSuchElementException when user is not found`() {
        val login = "unknownUser"

        every { userRepository.findByLogin(login) } returns Mono.empty()

        val exception = assertThrows<NoSuchElementException> {
            userService.getByLogin(login).block()
        }

        assertEquals("User with login $login not found", exception.message)
        verify { userRepository.findByLogin(login) }
    }

    @Test
    fun `should add user successfully`() {
        val request = RegisterDto(login = "newUser", password = "password123", role = Role.USER)
        val encodedPassword = "encodedPassword"
        val user = User(
            id = "1",
            login = "newUser",
            password = encodedPassword,
            role = Role.USER,
            registrationDate = LocalDate.now()
        )

        every { passwordEncoder.encode(request.password) } returns encodedPassword
        every { userRepository.save(any()) } returns Mono.just(user)

        val result = userService.addUser(request).block()

        assertNotNull(result)
        assertEquals(user, result)
        verify { passwordEncoder.encode(request.password) }
        verify { userRepository.save(any()) }
    }

    @Test
    fun `should return true when user exists`() {
        val login = "existingUser"

        every { userRepository.findByLogin(login) } returns Mono.just(
            User(
                id = "1",
                login = login,
                password = "password",
                role = Role.USER,
                registrationDate = LocalDate.now()
            )
        )

        val result = userService.checkIfExists(login).block()

        assertTrue(result!!)
        verify { userRepository.findByLogin(login) }
    }

}
