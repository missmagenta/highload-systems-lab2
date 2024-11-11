package itmo.highload.service

import itmo.highload.api.dto.PlaceDto
import itmo.highload.exceptions.EntityAlreadyExistsException
import itmo.highload.model.Place
import itmo.highload.model.PlaceMapper
import itmo.highload.repository.PlaceRepository
import itmo.highload.service.contract.FavoritesService
import itmo.highload.service.contract.FeedbackService
import itmo.highload.exceptions.EntityNotFoundException
import org.springframework.data.geo.Distance
import org.springframework.data.geo.Metrics
import org.springframework.data.geo.Point
import org.springframework.data.mongodb.core.geo.GeoJsonPoint
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class PlaceService(
    private val placeRepository: PlaceRepository,
    private val feedbackService: FeedbackService,
    private val favoritesService: FavoritesService,
) {

    fun getPlace(id: String): Mono<Place> = placeRepository.findById(id)
        .switchIfEmpty(Mono.error(EntityNotFoundException("Place with ID $id not found")))

    fun addPlace(ownerId: String, request: PlaceDto): Mono<Place> {
        val geoJsonPoint = GeoJsonPoint(request.coordinates.longitude, request.coordinates.latitude)

        return placeRepository.findByCoordinates(geoJsonPoint)
            .flatMap<Place> {
                Mono.error(EntityAlreadyExistsException("Place has already been added"))
            }
            .switchIfEmpty(
                placeRepository.save(PlaceMapper.toEntity(ownerId, request))
            )

    }

    fun updateDescription(ownerId: String, id: String, description: String): Mono<Place> {
        return getPlace(id).flatMap { place ->
            place.description = description
            placeRepository.save(place)
        }
    }


    fun updateName(ownerId: String, id: String, name: String): Mono<Place> {
        return getPlace(id).flatMap { place ->
            place.name = name
            placeRepository.save(place)
        }
    }

    fun getAllPlaces(): Flux<Place> {
        return placeRepository.findAll()
    }

    fun getPlacesNear(
        latitude: Double,
        longitude: Double,
        distanceKm: Double
    ): Flux<Place> {
        return placeRepository.findByCoordinatesNear(
            Point(longitude, latitude),
            Distance(distanceKm, Metrics.KILOMETERS)
        )
    }

    fun getPlacesNearByTag(
        latitude: Double,
        longitude: Double,
        distanceKm: Double,
        tag: String
    ): Flux<Place> {
        return placeRepository.findByCoordinatesNearAndTagsContains(
            Point(longitude, latitude),
            tag,
            Distance(distanceKm, Metrics.KILOMETERS)
        )
    }

    fun getPlacesNearByName(
        latitude: Double,
        longitude: Double,
        distanceKm: Double,
        name: String
    ): Flux<Place> {
        return placeRepository.findByCoordinatesNearAndNameContains(
            Point(longitude, latitude),
            name,
            Distance(distanceKm, Metrics.KILOMETERS)
        )
    }

    fun deletePlace(id: String, ownerId: String): Mono<Void> {
        return placeRepository.findByIdAndOwnersContains(id, ownerId)
            .switchIfEmpty(Mono.error(EntityNotFoundException("Place with ID $id and owner $ownerId not found")))
            .flatMap { existingPlace ->
                Mono.zip(
                    feedbackService.deleteFeedbacksForPlace(id),
                    favoritesService.deleteFavoritesForPlace(id)
                )
                    .then(placeRepository.delete(existingPlace))
            }
    }
}
