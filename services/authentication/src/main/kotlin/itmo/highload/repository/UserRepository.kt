package itmo.highload.repository

import itmo.highload.model.User
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface UserRepository : ReactiveMongoRepository<User, String> {
    fun findByLogin(login: String): Mono<User>

    fun save(user: User): Mono<User>
}
