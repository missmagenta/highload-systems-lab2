package itmo.highload

import io.mockk.Called
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import itmo.highload.api.dto.response.Coordinates
import itmo.highload.api.dto.response.PlaceResponse
import itmo.highload.repository.FavoritesRepository
import itmo.highload.service.FavoritesService
import itmo.highload.service.contract.PlaceService
import itmo.highload.service.contract.PlaceServiceFallback
import org.junit.jupiter.api.Test
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import itmo.highload.api.dto.CreateFavoritesRequest
import itmo.highload.api.dto.response.FavoritesResponse
import itmo.highload.exceptions.EntityAlreadyExistsException
import itmo.highload.exceptions.EntityNotFoundException
import itmo.highload.model.Favorite
import itmo.highload.model.Favorites
import itmo.highload.model.FavoritesMapper
import org.assertj.core.api.Assertions.assertThatThrownBy
import reactor.core.publisher.Flux
import kotlin.test.assertEquals

class FavoritesServiceTest {
    private val placeService: PlaceService = mockk()
    private val favoritesRepository: FavoritesRepository = mockk()
    private val placeServiceFallback: PlaceServiceFallback = PlaceServiceFallback()

    private val favoritesService = FavoritesService(favoritesRepository, placeService)

    @Test
    fun `should add to favorites when place exists and not already favorited`() {
        val favoriteRequest = CreateFavoritesRequest(placeId = "place1", favoriteType = "HOME")
        val userId = "user1"
        val placeResponse = PlaceResponse(
            id = "place1",
            name = "Place 1",
            coordinates = Coordinates(1.0, 2.0),
            owners = listOf("owner1"),
            tags = listOf("tag"),
            description = "Description"
        )
        val expectedFavorite = Favorites(userId = userId, placeId = "place1", favorites = Favorite.HOME)

        every { placeService.getPlace(favoriteRequest.placeId, any()) } returns Mono.just(placeResponse)
        every { favoritesRepository.findByUserIdAndPlaceId(userId, favoriteRequest.placeId) } returns Mono.empty()
        every { favoritesRepository.save(any()) } returns Mono.just(expectedFavorite)

        val result = favoritesService.addToFavorites("valid-token", favoriteRequest, userId)

        StepVerifier.create(result)
            .expectNext(expectedFavorite)
            .verifyComplete()

        verify { placeService.getPlace(favoriteRequest.placeId, "valid-token") }
        verify { favoritesRepository.findByUserIdAndPlaceId(userId, favoriteRequest.placeId) }
        verify { favoritesRepository.save(any()) }
    }

    @Test
    fun `should return error if place not found when adding to favorites`() {
        val favoriteRequest = CreateFavoritesRequest(placeId = "place1", favoriteType = "HOME")
        val userId = "user1"

        every { placeService.getPlace(favoriteRequest.placeId, any()) } returns Mono.empty()

        val result = favoritesService.addToFavorites("valid-token", favoriteRequest, userId)

        StepVerifier.create(result)
            .expectError(EntityNotFoundException::class.java)
            .verify()

        verify { placeService.getPlace(favoriteRequest.placeId, "valid-token") }
    }

    @Test
    fun `should return favorite when it exists`() {
        val favoriteId = "favorite1"
        val expectedFavorite = Favorites(userId = "user1", placeId = "place1", favorites = Favorite.HOME)

        every { favoritesRepository.findById(favoriteId) } returns Mono.just(expectedFavorite)

        val result = favoritesService.getFavorite(favoriteId)

        StepVerifier.create(result)
            .expectNext(expectedFavorite)
            .verifyComplete()

        verify { favoritesRepository.findById(favoriteId) }
    }

    @Test
    fun `should return error when favorite not found`() {
        val favoriteId = "favorite1"

        every { favoritesRepository.findById(favoriteId) } returns Mono.empty()

        val result = favoritesService.getFavorite(favoriteId)

        StepVerifier.create(result)
            .expectError(EntityNotFoundException::class.java)
            .verify()

        verify { favoritesRepository.findById(favoriteId) }
    }

    @Test
    fun `should return favorites for a user`() {
        val userId = "user1"
        val favorite1 = Favorites(userId = userId, placeId = "place1", favorites = Favorite.ENTERTAINMENT)
        val favorite2 = Favorites(userId = userId, placeId = "place2", favorites = Favorite.WORK)

        every { favoritesRepository.findByUserId(userId) } returns Flux.just(favorite1, favorite2)

        val result = favoritesService.getFavoritesByUser(userId)

        StepVerifier.create(result)
            .expectNext(favorite1)
            .expectNext(favorite2)
            .verifyComplete()

        verify { favoritesRepository.findByUserId(userId) }
    }

    @Test
    fun `should delete favorite when it exists`() {
        val favoriteId = "favorite1"
        val userId = "user1"

        every { favoritesRepository.existsByIdAndUserId(favoriteId, userId) } returns Mono.just(true)
        every { favoritesRepository.deleteById(favoriteId) } returns Mono.empty()

        val result = favoritesService.deleteFavorite(favoriteId, userId)

        StepVerifier.create(result)
            .verifyComplete()

        verify { favoritesRepository.existsByIdAndUserId(favoriteId, userId) }
        verify { favoritesRepository.deleteById(favoriteId) }
    }

    @Test
    fun `should return error when favorite does not exist for deletion`() {
        val favoriteId = "favorite1"
        val userId = "user1"

        every { favoritesRepository.existsByIdAndUserId(favoriteId, userId) } returns Mono.just(false)

        val result = favoritesService.deleteFavorite(favoriteId, userId)

        StepVerifier.create(result)
            .expectError(EntityNotFoundException::class.java)
            .verify()

        verify { favoritesRepository.existsByIdAndUserId(favoriteId, userId) }
    }

    @Test
    fun `PlaceService should return empty Mono when fallback is used directly`() {
        val placeId = "place3"
        val token = "validToken"

        val result = placeServiceFallback.getPlace(placeId, token)

        StepVerifier.create(result)
            .expectComplete()
            .verify()
    }

    @Test
    fun `should map CreateFavoritesRequest to Favorites entity`() {
        val userId = "user1"
        val favoriteRequest = CreateFavoritesRequest(placeId = "place1", favoriteType = "HOME")

        val expectedEntity = Favorites(
            userId = userId,
            placeId = "place1",
            favorites = Favorite.HOME
        )

        val result = FavoritesMapper.toEntity(userId, favoriteRequest)

        assertEquals(result, expectedEntity)
    }

    @Test
    fun `should map Favorites entity to FavoritesResponse`() {
        val favoritesEntity = Favorites(
            id = "favorite1",
            userId = "user1",
            placeId = "place1",
            favorites = Favorite.HOME
        )

        val expectedResponse = FavoritesResponse(
            id = "favorite1",
            userId = "user1",
            placeId = "place1",
            favoriteType = "HOME"
        )

        val result = FavoritesMapper.toResponse(favoritesEntity)

        assertEquals(result, expectedResponse)
    }

    @Test
    fun `should throw IllegalArgumentException for invalid favoriteType when mapping to entity`() {
        val userId = "user1"
        val invalidFavoriteRequest = CreateFavoritesRequest(placeId = "place1", favoriteType = "INVALID")

        assertThatThrownBy {
            FavoritesMapper.toEntity(userId, invalidFavoriteRequest)
        }.isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("No enum constant")
    }

    @Test
    fun `should return error when no favorites exist for placeId`() {
        val placeId = "place1"

        every { favoritesRepository.existsByPlaceId(placeId) } returns Mono.just(false)

        val result = favoritesService.deleteFavoritesForPlace(placeId)

        StepVerifier.create(result)
            .expectError(EntityNotFoundException::class.java)
            .verify()

        verify { favoritesRepository.existsByPlaceId(placeId) }
    }

    @Test
    fun `should delete favorites for placeId when favorites exist`() {
        val placeId = "place1"

        every { favoritesRepository.existsByPlaceId(placeId) } returns Mono.just(true)
        every { favoritesRepository.deleteAllByPlaceId(placeId) } returns Mono.empty()

        val result = favoritesService.deleteFavoritesForPlace(placeId)

        StepVerifier.create(result)
            .verifyComplete()

        verify { favoritesRepository.existsByPlaceId(placeId) }
        verify { favoritesRepository.deleteAllByPlaceId(placeId) }
    }

    @Test
    fun `should return error when favorite already exists`() {
        val token = "valid_token"
        val userId = "user1"
        val placeId = "place1"
        val favoriteType = "HOME"
        val favoriteRequest = CreateFavoritesRequest(placeId = placeId, favoriteType = favoriteType)

        val existingFavorite = Favorites(
            id = "fav1",
            userId = userId,
            placeId = placeId,
            favorites = Favorite.valueOf(favoriteType)
        )

        val place = PlaceResponse(
            id = placeId,
            name = "Place",
            coordinates = Coordinates(1.0, 1.0),
            owners = listOf(),
            tags = listOf(),
            description = "A place"
        )
        every { placeService.getPlace(placeId, token) } returns Mono.just(place)

        every { favoritesRepository.findByUserIdAndPlaceId(userId, placeId) } returns Mono.just(existingFavorite)

        every { favoritesRepository.save(any()) } returns Mono.empty()
        val result = favoritesService.addToFavorites(token, favoriteRequest, userId)

        StepVerifier.create(result)
            .expectError(EntityAlreadyExistsException::class.java)
            .verify()

        verify { placeService.getPlace(placeId, token) }
        verify { favoritesRepository.findByUserIdAndPlaceId(userId, placeId) }
        verify { favoritesRepository.save(any()) wasNot Called }
    }
}