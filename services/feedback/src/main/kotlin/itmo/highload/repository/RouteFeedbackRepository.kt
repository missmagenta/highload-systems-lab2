package itmo.highload.repository

import itmo.highload.model.RouteFeedback
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

@Repository
interface RouteFeedbackRepository: ReactiveMongoRepository<RouteFeedback, String> {
    fun findByRouteId(routeId: String): Flux<RouteFeedback>
    fun deleteAllByRouteId(routeId: String)
}