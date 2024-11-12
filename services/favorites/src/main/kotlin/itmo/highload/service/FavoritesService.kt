package itmo.highload.service

import itmo.highload.api.dto.CreateFavoritesRequest
import itmo.highload.exceptions.EntityAlreadyExistsException
import itmo.highload.exceptions.EntityNotFoundException
import itmo.highload.model.Favorite
import itmo.highload.model.Favorites
import itmo.highload.repository.FavoritesRepository
import itmo.highload.service.contract.PlaceService
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class FavoritesService(
    private val favoritesRepository: FavoritesRepository,
    private val placeService: PlaceService,
) {

    fun addToFavorites(token: String, favorite: CreateFavoritesRequest, userId: String): Mono<Favorites> {
        return placeService.getPlace(favorite.placeId, token)
            .switchIfEmpty(Mono.error(EntityNotFoundException("Place with ID ${favorite.placeId} not found")))
            .flatMap {
                favoritesRepository.findByUserIdAndPlaceId(userId, favorite.placeId)
                    .flatMap<Favorites> {
                        Mono.error(EntityAlreadyExistsException("Fav already added"))
                    }
                    .switchIfEmpty(
                        favoritesRepository.save(
                            Favorites(
                                userId = userId,
                                placeId = favorite.placeId,
                                favorites = Favorite.valueOf(favorite.favoriteType)
                            )
                        )
                    )
            }
    }

    fun getFavorite(id: String): Mono<Favorites> {
        return favoritesRepository.findById(id)
            .switchIfEmpty(Mono.error(EntityNotFoundException("Favorite with ID $id not found")))
    }

    fun getFavoritesByUser(userId: String): Flux<Favorites> {
        return favoritesRepository.findByUserId(userId)
    }

    fun deleteFavorite(id: String, userId: String): Mono<Void> {
        return favoritesRepository.existsByIdAndUserId(id, userId)
            .flatMap { exists ->
                if (!exists) {
                    Mono.error(EntityNotFoundException("Favorite with id $id not found"))
                } else {
                    favoritesRepository.deleteById(id)
                }
            }
    }

    fun deleteFavoritesForPlace(placeId: String): Mono<Void> {
    return favoritesRepository.existsByPlaceId(placeId)
        .flatMap { exists ->
            if (!exists) {
                Mono.error(EntityNotFoundException("No favorites found for placeId $placeId"))
            } else {
                favoritesRepository.deleteAllByPlaceId(placeId)
            }
        }
    }
}
