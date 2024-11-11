package itmo.highload.model

import itmo.highload.api.dto.CreateFavoritesRequest
import itmo.highload.api.dto.response.FavoritesResponse

object FavoritesMapper {
    fun toEntity(userId: String, request: CreateFavoritesRequest): Favorites {
        return Favorites(
            userId = userId,
            placeId = request.placeId,
            favorites = Favorite.valueOf(request.favoriteType)
        )
    }

    fun toResponse(favorites: Favorites): FavoritesResponse {
        return FavoritesResponse(
            id = favorites.id!!,
            userId = favorites.userId,
            placeId = favorites.placeId,
            favoriteType = favorites.favorites.toString(),
        )
    }
}