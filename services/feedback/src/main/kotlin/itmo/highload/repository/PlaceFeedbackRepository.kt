package itmo.highload.repository

import itmo.highload.model.PlaceFeedback
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

@Repository
interface PlaceFeedbackRepository: ReactiveMongoRepository<PlaceFeedback, String> {
    fun findByPlaceId(routeId: String): Flux<PlaceFeedback>
    fun deleteAllByPlaceId(placeId: String)
}

