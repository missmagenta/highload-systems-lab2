package itmo.highload.repository

import itmo.highload.model.Route
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

@Repository
interface RouteRepository : ReactiveMongoRepository<Route, String> {
    override fun findAll(): Flux<Route>
    fun findAllByPlacesContains(placeId: String): Flux<Route>
}
