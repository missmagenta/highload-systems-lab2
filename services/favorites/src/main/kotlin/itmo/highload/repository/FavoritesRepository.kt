package itmo.highload.repository

import itmo.highload.model.Favorites
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
interface FavoritesRepository : ReactiveMongoRepository<Favorites, String> {
    fun findByUserId(userId: String): Flux<Favorites>
    fun findByUserIdAndPlaceId(userId: String, placeId: String): Mono<Favorites>
    fun existsByIdAndUserId(id: String, userId: String): Mono<Boolean>
    fun existsByPlaceId(placeId: String): Mono<Boolean>
    fun deleteAllByPlaceId(placeId: String): Mono<Void>
}

