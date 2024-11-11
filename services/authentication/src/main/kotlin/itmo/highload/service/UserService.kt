package itmo.highload.service

import itmo.highload.dto.RegisterDto
import itmo.highload.model.User
import itmo.highload.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.time.LocalDate

@Service
class UserService(
    private val userRepository: UserRepository, private val encoder: PasswordEncoder
) {

    fun getByLogin(login: String): Mono<User> = userRepository.findByLogin(login)
        .switchIfEmpty(Mono.error(NoSuchElementException("User with login $login not found")))

    fun addUser(request: RegisterDto): Mono<User> {
        val user = User(
            login = request.login,
            password = encoder.encode(request.password),
            role = request.role,
            registrationDate = LocalDate.now()
        )
        return userRepository.save(user)
    }

    fun checkIfExists(login: String): Mono<Boolean> = userRepository.findByLogin(login).hasElement()
}
