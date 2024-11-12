package itmo.highload.repository

import itmo.highload.model.Place
import org.springframework.data.geo.Distance
import org.springframework.data.geo.Point
import org.springframework.data.mongodb.core.geo.GeoJsonPoint
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
interface PlaceRepository : ReactiveMongoRepository<Place, String> {
    fun findByCoordinatesNear(
        coordinates: Point,
        distance: Distance
    ): Flux<Place>

    fun findByCoordinatesNearAndTagsContains(
        coordinates: Point,
        tag: String,
        distance: Distance
    ): Flux<Place>

    fun findByCoordinatesNearAndNameContains(
        coordinates: Point,
        name: String,
        distance: Distance
    ): Flux<Place>

    fun findByCoordinates(coordinates: GeoJsonPoint): Mono<Place>

    fun findByIdAndOwnersContains(id: String, ownerId: String): Mono<Place>
}

