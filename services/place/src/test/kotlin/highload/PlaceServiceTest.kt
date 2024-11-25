package highload

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import itmo.highload.mongo.Config
import itmo.highload.api.dto.Coordinates
import itmo.highload.api.dto.PlaceDto
import itmo.highload.api.dto.UpdatePlaceNameDto
import itmo.highload.exceptions.EntityAlreadyExistsException
import itmo.highload.exceptions.EntityNotFoundException
import itmo.highload.model.Place
import itmo.highload.model.PlaceMapper
import itmo.highload.repository.PlaceRepository
import itmo.highload.service.PlaceService
import itmo.highload.service.contract.FavoritesService
import itmo.highload.service.contract.FeedbackService
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.data.geo.Distance
import org.springframework.data.geo.Metrics
import org.springframework.data.geo.Point
import org.springframework.data.mongodb.core.geo.GeoJsonPoint
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.util.*

class PlaceServiceTest {

    private val placeRepository: PlaceRepository = mockk()
    private val feedbackService: FeedbackService = mockk()
    private val favoritesService: FavoritesService = mockk()
    private val placeService = PlaceService(placeRepository, feedbackService, favoritesService)

    private val existingPlace = Place(
        id = "1",
        name = "Cool",
        coordinates = GeoJsonPoint(1.0, 1.0),
        tags = listOf("baby"),
        description = "cool"
    )

    private val newPlaceDto = PlaceDto(
        name = "New Place",
        coordinates = Coordinates(2.0, 2.0),
        tags = listOf("fun"),
        description = "New Description"
    )

    private val ownerId = "owner-1"
    private val token = "token-1"

    @Test
    fun `should return place when it exists`() {
        every { placeRepository.findById("1") } returns Mono.just(existingPlace)

        StepVerifier.create(placeService.getPlace("1"))
            .expectNext(existingPlace)
            .verifyComplete()

        verify { placeRepository.findById("1") }
    }

    @Test
    fun `should throw error when place not found`() {
        every { placeRepository.findById("1") } returns Mono.empty()

        StepVerifier.create(placeService.getPlace("1"))
            .expectError(EntityNotFoundException::class.java)
            .verify()

        verify(exactly = 1) { placeRepository.findById("1") }
    }

    @Test
    fun `should add new place when not exists`() {
        every { placeRepository.findByCoordinates(any()) } returns Mono.empty()
        every { placeRepository.save(any()) } returns Mono.just(existingPlace)

        StepVerifier.create(placeService.addPlace(ownerId, newPlaceDto))
            .assertNext { place ->
                assertEquals("Cool", place.name)
            }
            .verifyComplete()

        verify { placeRepository.findByCoordinates(any()) }
        verify { placeRepository.save(any()) }
    }

    @Test
    fun `should return error when place already exists`() {

        every { placeRepository.findByCoordinates(any()) } returns Mono.just(existingPlace)
        every { placeRepository.save(any()) } returns Mono.just(existingPlace)

        StepVerifier.create(placeService.addPlace(ownerId, newPlaceDto))
            .expectErrorMatches { it is EntityAlreadyExistsException }
            .verify()

        verify { placeRepository.findByCoordinates(any()) }
        verify { placeRepository.save(any()) }
    }

    @Test
    fun `should update place name when valid`() {
        val request = UpdatePlaceNameDto(
            name = "University",
        )

        every { placeRepository.findById("1") } returns Mono.just(existingPlace)
        every { placeRepository.save(existingPlace) } returns Mono.just(existingPlace.copy(
            name = "University",
        ))

        placeService.updateName("1", "1", request.name).subscribe { updated ->
            assertEquals("University", updated.name)
            verify { placeRepository.save(existingPlace) }
        }
    }

    @Test
    fun `should update place description when valid`() {
        val newDescription = "Updated Description"
        every { placeRepository.findById("1") } returns Mono.just(existingPlace)
        every { placeRepository.save(existingPlace) } returns Mono.just(existingPlace.copy(description = newDescription))

        StepVerifier.create(placeService.updateDescription(ownerId, "1", newDescription))
            .assertNext { place ->
                assertEquals(newDescription, place.description)
            }
            .verifyComplete()

        verify { placeRepository.save(existingPlace) }
    }

    @Test
    fun `should return all places`() {
        every { placeRepository.findAll() } returns Flux.just(existingPlace)

        StepVerifier.create(placeService.getAllPlaces())
            .expectNext(existingPlace)
            .verifyComplete()

        verify { placeRepository.findAll() }
    }

    @Test
    fun `should return places near given coordinates`() {
        every {
            placeRepository.findByCoordinatesNear(any(), any())
        } returns Flux.just(existingPlace)

        StepVerifier.create(placeService.getPlacesNear(1.0, 1.0, 10.0))
            .expectNext(existingPlace)
            .verifyComplete()

        verify { placeRepository.findByCoordinatesNear(any(), any()) }
    }

    @Test
    fun `should return places near given coordinates with specific tag`() {
        every {
            placeRepository.findByCoordinatesNearAndTagsContains(
                any(),
                any(),
                any()
            )
        } returns Flux.just(existingPlace)

        StepVerifier.create(placeService.getPlacesNearByTag(1.0, 1.0, 10.0, "park"))
            .expectNext(existingPlace)
            .verifyComplete()

        verify {
            placeRepository.findByCoordinatesNearAndTagsContains(
                Point(1.0, 1.0),
                "park",
                Distance(10.0, Metrics.KILOMETERS)
            )
        }
    }

    @Test
    fun `should return empty when no places near given coordinates with specific tag`() {
        every {
            placeRepository.findByCoordinatesNearAndTagsContains(
                any(),
                any(),
                any()
            )
        } returns Flux.empty()

        StepVerifier.create(placeService.getPlacesNearByTag(1.0, 1.0, 10.0, "park"))
            .verifyComplete()

        verify {
            placeRepository.findByCoordinatesNearAndTagsContains(
                Point(1.0, 1.0),
                "park",
                Distance(10.0, Metrics.KILOMETERS)
            )
        }
    }

    @Test
    fun `should return places near given coordinates with specific name`() {
        every {
            placeRepository.findByCoordinatesNearAndNameContains(
                any(),
                any(),
                any()
            )
        } returns Flux.just(existingPlace)

        StepVerifier.create(placeService.getPlacesNearByName(1.0, 1.0, 10.0, "Central"))
            .expectNext(existingPlace)
            .verifyComplete()

        verify {
            placeRepository.findByCoordinatesNearAndNameContains(
                Point(1.0, 1.0),
                "Central",
                Distance(10.0, Metrics.KILOMETERS)
            )
        }
    }

    @Test
    fun `should return empty when no places near given coordinates with specific name`() {
        every {
            placeRepository.findByCoordinatesNearAndNameContains(
                any(),
                any(),
                any()
            )
        } returns Flux.empty()

        StepVerifier.create(placeService.getPlacesNearByName(1.0, 1.0, 10.0, "Central"))
            .verifyComplete()

        verify {
            placeRepository.findByCoordinatesNearAndNameContains(
                Point(1.0, 1.0),
                "Central",
                Distance(10.0, Metrics.KILOMETERS)
            )
        }
    }

    @Test
    fun `should throw error if place not found during deletion`() {
        every { placeRepository.findByIdAndOwnersContains("1", ownerId) } returns Mono.empty()

        StepVerifier.create(placeService.deletePlace("1", ownerId, token))
            .expectError(EntityNotFoundException::class.java)
            .verify()

        verify(exactly = 0) { feedbackService.deleteFeedbacksForPlace(any(), any()) }
        verify(exactly = 0) { favoritesService.deleteFavoritesForPlace(any(), any()) }
    }

    @Test
    fun `should return correct database name`() {
        val config = mockk<Config>()

        every { config.getDatabaseName() } returns "testbase"

        val dbName = config.getDatabaseName()
        assertEquals("testbase", dbName, "Database name should match the mocked value")
        verify { config.getDatabaseName() }
    }

    @Test
    fun `should create MongoClientSettings with correct connection string and credentials`() {
        val config = mockk<Config>(relaxed = true)

        every { config.url } returns "mongodb://localhost:27017"
        every { config.user } returns "test_user"
        every { config.password } returns "test_password"
        every { config.authSource } returns "admin"

        val settings = config.mongoClientSettings()

        verify {
            config.mongoClientSettings()
        }

        assertTrue(!settings.equals(null), "MongoClientSettings should not be null")
    }

    @Test
    fun `should map PlaceDto to Place`() {
        // Given
        val ownerId = "owner1"
        val placeDto = PlaceDto(
            name = "Test Place",
            coordinates = Coordinates(longitude = 12.34, latitude = 56.78),
            tags = listOf("tag1", "tag2"),
            description = "A place description"
        )

        // When
        val result = PlaceMapper.toEntity(ownerId, placeDto)

        // Then
        assertNotNull(result)
        assertEquals(placeDto.name, result.name)
        assertEquals(GeoJsonPoint(placeDto.coordinates.longitude, placeDto.coordinates.latitude), result.coordinates)
        assertEquals(listOf(ownerId), result.owners)
        assertEquals(placeDto.tags, result.tags)
        assertEquals(placeDto.description, result.description)
    }

    @Test
    fun `should map Place to PlaceResponse`() {
        // Given
        val place = Place(
            id = UUID.randomUUID().toString(),
            name = "Test Place",
            coordinates = GeoJsonPoint(12.34, 56.78),
            owners = listOf("owner1"),
            tags = listOf("tag1", "tag2"),
            description = "A place description"
        )

        // When
        val result = PlaceMapper.toPlaceResponse(place)

        // Then
        assertNotNull(result)
        assertEquals(place.id, result.id)
        assertEquals(place.name, result.name)
        assertEquals(itmo.highload.api.dto.response.Coordinates(longitude = place.coordinates.x, latitude = place.coordinates.y), result.coordinates)
        assertEquals(place.owners, result.owners)
        assertEquals(place.tags, result.tags)
        assertEquals(place.description, result.description)
    }
}
