package highload

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import itmo.highload.api.dto.UpdatePlaceNameDto
import itmo.highload.model.Place
import itmo.highload.repository.PlaceRepository
import itmo.highload.service.PlaceService
import itmo.highload.service.contract.FavoritesService
import itmo.highload.service.contract.FeedbackService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.data.mongodb.core.geo.GeoJsonPoint
import reactor.core.publisher.Mono
import reactor.kotlin.test.test

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
}
