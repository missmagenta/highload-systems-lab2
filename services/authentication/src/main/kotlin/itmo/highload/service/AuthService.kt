package itmo.highload.service

import itmo.highload.dto.RegisterDto
import itmo.highload.model.User
import itmo.highload.security.dto.JwtResponse
import itmo.highload.security.jwt.JwtUtils
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class AuthService(
    private val jwtProvider: JwtUtils, private val userService: UserService, private val encoder: PasswordEncoder
) {

    fun login(login: String, password: String): Mono<JwtResponse> = userService.getByLogin(login).flatMap { user ->
        if (!encoder.matches(password, user.password)) {
            Mono.error(BadCredentialsException("Wrong password"))
        } else {
            val accessToken: String = jwtProvider.generateAccessToken(user.login, user.role, user.id!!)
            Mono.just(JwtResponse(accessToken, user.role))
        }
    }

    fun register(request: RegisterDto): Mono<User> = userService.addUser(request)

    fun checkIfUserExists(login: String): Mono<Boolean> = userService.checkIfExists(login)
}
