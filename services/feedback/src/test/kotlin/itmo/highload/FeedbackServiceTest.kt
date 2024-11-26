package itmo.highload

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import itmo.highload.api.dto.CreatePlaceFeedbackRequest
import itmo.highload.api.dto.CreateRouteFeedbackRequest
import itmo.highload.api.dto.response.Coordinates
import itmo.highload.api.dto.response.PlaceResponse
import itmo.highload.api.dto.response.RouteResponse
import itmo.highload.exceptions.EntityNotFoundException
import itmo.highload.model.FeedbackMapper
import itmo.highload.model.Grade
import itmo.highload.model.PlaceFeedback
import itmo.highload.model.RouteFeedback
import itmo.highload.repository.PlaceFeedbackRepository
import itmo.highload.repository.RouteFeedbackRepository
import itmo.highload.service.FeedbackService
import itmo.highload.service.contract.PlaceService
import itmo.highload.service.contract.PlaceServiceFallback
import itmo.highload.service.contract.RouteService
import itmo.highload.service.contract.RouteServiceFallback
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.data.mongodb.core.geo.GeoJsonPoint
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

class FeedbackServiceTest {
    private val placeService: PlaceService = mockk()
    private val routeService: RouteService = mockk()
    private val routeFeedbackRepository: RouteFeedbackRepository = mockk()
    private val placeFeedbackRepository: PlaceFeedbackRepository = mockk()
    private val placeServiceFallback: PlaceServiceFallback = PlaceServiceFallback()
    private val routeServiceFallback: RouteServiceFallback = RouteServiceFallback()


    private val feedbackService = FeedbackService(routeFeedbackRepository, placeFeedbackRepository, routeService, placeService)

    private val routeId = "route123"
    private val placeId = "place123"
    private val userId = "user123"
    private val grade = 5
    private val feedbackId = "feedback123"

    @Test
    fun `should create route feedback when route exists`() {
        val feedback = CreateRouteFeedbackRequest("route1", 5)
        val route = RouteResponse(id = "route1", name = "Route 1", description = "Description", places = listOf())
        val expectedFeedback = RouteFeedback(routeId = "route1", grade = Grade(userId = "user1", grade = 5))

        every { routeService.getRoute(feedback.routeId, any()) } returns Mono.just(route)
        every { routeFeedbackRepository.save(any()) } returns Mono.just(expectedFeedback)

        val result = feedbackService.createRouteFeedback(feedback, "token", "userId")

        StepVerifier.create(result)
            .expectNext(expectedFeedback)
            .verifyComplete()

        verify { routeService.getRoute(feedback.routeId, "token") }
        verify { routeFeedbackRepository.save(any()) }
    }

    @Test
    fun `should return error when route does not exist`() {
        val feedback = CreateRouteFeedbackRequest("route1", 5)

        every { routeService.getRoute(feedback.routeId, any()) } returns Mono.empty()

        val result = feedbackService.createRouteFeedback(feedback, "token", "userId")

        StepVerifier.create(result)
            .expectError(EntityNotFoundException::class.java)
            .verify()

        verify { routeService.getRoute(feedback.routeId, "token") }
    }

    @Test
    fun `should get route feedbacks`() {
        val routeId = "route1"
        val feedback = RouteFeedback(routeId = routeId, grade = Grade(userId = "user1", grade = 5))

        every { routeFeedbackRepository.findByRouteId(routeId) } returns Flux.just(feedback)

        val result = feedbackService.getRouteFeedbacks(routeId)

        StepVerifier.create(result)
            .expectNext(feedback)
            .verifyComplete()

        verify { routeFeedbackRepository.findByRouteId(routeId) }
    }

    @Test
    fun `should delete route feedback`() {
        val feedbackId = "feedback1"
        val feedback = RouteFeedback(routeId = "route1", grade = Grade(userId = "user1", grade = 5))

        every { routeFeedbackRepository.findById(feedbackId) } returns Mono.just(feedback)
        every { routeFeedbackRepository.deleteById(feedbackId) } returns Mono.empty()

        val result = feedbackService.deleteRouteFeedback(feedbackId)

        StepVerifier.create(result)
            .verifyComplete()

        verify { routeFeedbackRepository.findById(feedbackId) }
        verify { routeFeedbackRepository.deleteById(feedbackId) }
    }

    @Test
    fun `should return error when route feedback does not exist`() {
        val feedbackId = "feedback1"

        every { routeFeedbackRepository.findById(feedbackId) } returns Mono.empty()

        val result = feedbackService.deleteRouteFeedback(feedbackId)

        StepVerifier.create(result)
            .expectError(EntityNotFoundException::class.java)
            .verify()

        verify { routeFeedbackRepository.findById(feedbackId) }
    }

    @Test
    fun `should create place feedback when place exists`() {
        val feedback = CreatePlaceFeedbackRequest("place1",  5)
        val place = PlaceResponse(id = "place1", name = "Place 1", coordinates = Coordinates(12.34, 56.78), owners = listOf(), tags = listOf(), description = "Description")
        val expectedFeedback = PlaceFeedback(placeId = "place1", grade = Grade(userId = "user1", grade = 5))

        every { placeService.getPlace(feedback.placeId, any()) } returns Mono.just(place)
        every { placeFeedbackRepository.save(any()) } returns Mono.just(expectedFeedback)

        val result = feedbackService.createPlaceFeedback(feedback, "token", "userId")

        StepVerifier.create(result)
            .expectNext(expectedFeedback)
            .verifyComplete()

        verify { placeService.getPlace(feedback.placeId, "token") }
        verify { placeFeedbackRepository.save(any()) }
    }

    @Test
    fun `should return error when place does not exist`() {
        val feedback = CreatePlaceFeedbackRequest("place1", 5)

        every { placeService.getPlace(feedback.placeId, any()) } returns Mono.empty()

        val result = feedbackService.createPlaceFeedback(feedback, "token", "userId")

        StepVerifier.create(result)
            .expectError(EntityNotFoundException::class.java)
            .verify()

        verify { placeService.getPlace(feedback.placeId, "token") }
    }

    @Test
    fun `should get place feedbacks`() {
        val placeId = "place1"
        val feedback = PlaceFeedback(placeId = placeId, grade = Grade(userId = "user1", grade = 5))

        every { placeFeedbackRepository.findByPlaceId(placeId) } returns Flux.just(feedback)

        val result = feedbackService.getPlaceFeedbacks(placeId)

        StepVerifier.create(result)
            .expectNext(feedback)
            .verifyComplete()

        verify { placeFeedbackRepository.findByPlaceId(placeId) }
    }

    @Test
    fun `should delete place feedback`() {
        val feedbackId = "feedback1"
        val feedback = PlaceFeedback(placeId = "place1", grade = Grade(userId = "user1", grade = 5))

        every { placeFeedbackRepository.findById(feedbackId) } returns Mono.just(feedback)
        every { placeFeedbackRepository.deleteById(feedbackId) } returns Mono.empty()

        val result = feedbackService.deletePlaceFeedback(feedbackId)

        StepVerifier.create(result)
            .verifyComplete()

        verify { placeFeedbackRepository.findById(feedbackId) }
        verify { placeFeedbackRepository.deleteById(feedbackId) }
    }

    @Test
    fun `should return error when place feedback does not exist`() {
        val feedbackId = "feedback1"

        every { placeFeedbackRepository.findById(feedbackId) } returns Mono.empty()

        val result = feedbackService.deletePlaceFeedback(feedbackId)

        StepVerifier.create(result)
            .expectError(EntityNotFoundException::class.java)
            .verify()

        verify { placeFeedbackRepository.findById(feedbackId) }
    }

    @Test
    fun `should delete all feedbacks for place`() {
        val placeId = "place1"

        every { placeFeedbackRepository.existsByPlaceId(placeId) } returns Mono.just(true)
        every { placeFeedbackRepository.deleteAllByPlaceId(placeId) } returns Mono.empty()

        val result = feedbackService.deleteFeedbacksForPlace(placeId)

        StepVerifier.create(result)
            .verifyComplete()

        verify { placeFeedbackRepository.existsByPlaceId(placeId) }
        verify { placeFeedbackRepository.deleteAllByPlaceId(placeId) }
    }

    @Test
    fun `should return error when no feedbacks for place`() {
        val placeId = "place1"

        every { placeFeedbackRepository.existsByPlaceId(placeId) } returns Mono.just(false)

        val result = feedbackService.deleteFeedbacksForPlace(placeId)

        StepVerifier.create(result)
            .expectError(EntityNotFoundException::class.java)
            .verify()

        verify { placeFeedbackRepository.existsByPlaceId(placeId) }
    }

    @Test
    fun `should delete all feedbacks for route`() {
        val routeId = "route1"

        every { routeFeedbackRepository.existsByRouteId(routeId) } returns Mono.just(true)
        every { routeFeedbackRepository.deleteAllByRouteId(routeId) } returns Mono.empty()

        val result = feedbackService.deleteFeedbacksForRoute(routeId)

        StepVerifier.create(result)
            .verifyComplete()

        verify { routeFeedbackRepository.existsByRouteId(routeId) }
        verify { routeFeedbackRepository.deleteAllByRouteId(routeId) }
    }

    @Test
    fun `should return error when no feedbacks for route`() {
        val routeId = "route1"

        every { routeFeedbackRepository.existsByRouteId(routeId) } returns Mono.just(false)

        val result = feedbackService.deleteFeedbacksForRoute(routeId)

        StepVerifier.create(result)
            .expectError(EntityNotFoundException::class.java)
            .verify()

        verify { routeFeedbackRepository.existsByRouteId(routeId) }
    }

    @Test
    fun `RouteService should return empty Mono when fallback is used directly`() {
        val placeId = "place3"
        val token = "validToken"

        val result = routeServiceFallback.getRoute(placeId, token)

        StepVerifier.create(result)
            .expectComplete()
            .verify()

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
    fun `should map RouteFeedback entity to RouteFeedbackResponse`() {
        val entity = RouteFeedback(
            id = "feedback1",
            routeId = "route1",
            grade = Grade(userId = "user1", grade = 5)
        )

        val result = FeedbackMapper.toRouteResponse(entity)

        assertEquals("feedback1", result.id)
        assertEquals("route1", result.routeId)
        assertEquals("user1", result.userId)
        assertEquals(5, result.grade)
    }


    @Test
    fun `should map PlaceFeedback entity to PlaceFeedbackResponse`() {
        val entity = PlaceFeedback(
            id = "feedback2",
            placeId = "place1",
            grade = Grade(userId = "user2", grade = 4)
        )

        val result = FeedbackMapper.toPlaceResponse(entity)

        assertEquals("feedback2", result.id)
        assertEquals("place1", result.placeId)
        assertEquals("user2", result.userId)
        assertEquals(4, result.grade)
    }
}