package itmo.highload

import io.mockk.mockk
import io.mockk.every
import io.mockk.verify
import itmo.highload.api.dto.CreateRouteRequest
import itmo.highload.api.dto.response.PlaceResponse
import itmo.highload.exceptions.EntityNotFoundException
import itmo.highload.model.Route
import itmo.highload.model.RouteRequestMapper
import itmo.highload.repository.RouteRepository
import itmo.highload.service.RouteService
import itmo.highload.service.contract.PlaceService
import itmo.highload.service.contract.PlaceServiceFallback
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.util.*

class RouteServiceTest {

    private val placeService: PlaceService = mockk()
    private val routeRepository: RouteRepository = mockk()
    private val placeServiceFallback: PlaceServiceFallback = PlaceServiceFallback()
    private val routeService = RouteService(routeRepository, placeService)

    @Test
    fun `should return route by id`() {
        val routeId = "route1"
        val expectedRoute = Route(id = routeId, name = "Test Route", description = "A sample route", places = listOf("place1"))

        every { routeRepository.findById(routeId) } returns Mono.just(expectedRoute)

        val result = routeService.getRoute(routeId)

        StepVerifier.create(result)
            .expectNext(expectedRoute)
            .verifyComplete()

        verify { routeRepository.findById(routeId) }
    }

    @Test
    fun `should throw EntityNotFoundException when route is not found`() {
        val routeId = "invalidRoute"
        every { routeRepository.findById(routeId) } returns Mono.empty()

        val result = routeService.getRoute(routeId)

        StepVerifier.create(result)
            .expectError(EntityNotFoundException::class.java)
            .verify()

        verify { routeRepository.findById(routeId) }
    }

    @Test
    fun `should return all routes`() {
        val routes = listOf(
            Route(id = "route1", name = "Route 1", description = "Description 1", places = listOf("place1")),
            Route(id = "route2", name = "Route 2", description = "Description 2", places = listOf("place2"))
        )
        every { routeRepository.findAll() } returns Flux.fromIterable(routes)

        val result = routeService.getAllRoutes()

        StepVerifier.create(result)
            .expectNextSequence(routes)
            .verifyComplete()

        verify { routeRepository.findAll() }
    }

    @Test
    fun `should throw EntityNotFoundException when any place is not found during route creation`() {
        val request = CreateRouteRequest(
            name = "New Route",
            description = "A new route for testing",
            places = listOf("invalidPlace")
        )
        val token = "testToken"

        every { placeService.getPlace("invalidPlace", token) } returns Mono.empty()

        val result = routeService.createRoute(request, token)

        StepVerifier.create(result)
            .expectError(EntityNotFoundException::class.java)
            .verify()

        verify { placeService.getPlace("invalidPlace", token) }
    }

    @Test
    fun `should find routes by place id`() {
        val placeId = "place1"
        val routes = listOf(
            Route(id = "route1", name = "Route 1", description = "Description 1", places = listOf(placeId)),
            Route(id = "route2", name = "Route 2", description = "Description 2", places = listOf(placeId))
        )
        every { routeRepository.findAllByPlacesContains(placeId) } returns Flux.fromIterable(routes)

        val result = routeService.findRoutesByPlaceContains(placeId)

        StepVerifier.create(result)
            .expectNextSequence(routes)
            .verifyComplete()

        verify { routeRepository.findAllByPlacesContains(placeId) }
    }

    @Test
    fun `should create route successfully when all places are valid`() {
        val placeIds = listOf("place1", "place2")
        val request = CreateRouteRequest(name = "Test Route", description = "A sample route", places = placeIds)
        val route = Route(id = "route1", name = request.name, description = request.description, places = placeIds)

        placeIds.forEach { placeId ->
            val placeMock = mockk<PlaceResponse> {
                every { id } returns placeId
                every { name } returns "Place $placeId"
            }
            every { placeService.getPlace(placeId, any()) } returns Mono.just(placeMock)
        }

        every { routeRepository.save(any()) } returns Mono.just(route)

        val result = routeService.createRoute(request, "someToken")

        StepVerifier.create(result)
            .expectNext(route)
            .verifyComplete()

        placeIds.forEach { placeId ->
            verify { placeService.getPlace(placeId, any()) }
        }
        verify { routeRepository.save(any()) }
    }

    @Test
    fun `should delete route successfully when route exists`() {
        val routeId = "route1"
        val userId = "user1"

        every { routeRepository.findById(routeId) } returns Mono.just(Route(id = routeId, name = "Test Route", description = "A sample route", places = listOf()))
        every { routeRepository.deleteById(routeId) } returns Mono.empty()

        val result = routeService.deleteRoute(routeId, userId)

        StepVerifier.create(result)
            .verifyComplete()

        verify { routeRepository.findById(routeId) }
        verify { routeRepository.deleteById(routeId) }
    }

    @Test
    fun `should return error when route not found`() {
        val routeId = "route1"
        val userId = "user1"

        every { routeRepository.findById(routeId) } returns Mono.empty()

        val result = routeService.deleteRoute(routeId, userId)

        StepVerifier.create(result)
            .expectError(EntityNotFoundException::class.java)
            .verify()

        verify { routeRepository.findById(routeId) }
        verify(exactly = 0) { routeRepository.deleteById(routeId) }
    }


    @Test
    fun `should return empty Mono when fallback is used directly`() {
        val placeId = "place3"
        val token = "validToken"

        val result = placeServiceFallback.getPlace(placeId, token)

        StepVerifier.create(result)
            .expectComplete()
            .verify()

    }

    @Test
    fun `should map CreateRouteRequest to Route`() {
        val request = CreateRouteRequest(
            name = "Test Route",
            description = "A route description",
            places = listOf("place1", "place2")
        )

        val result = RouteRequestMapper.toEntity(request)

        assertNotNull(result)
        assertEquals(request.name, result.name)
        assertEquals(request.description, result.description)
        assertEquals(request.places, result.places)
    }

    @Test
    fun `should map Route to RouteResponse`() {
        val route = Route(
            id = UUID.randomUUID().toString(),
            name = "Test Route",
            description = "A route description",
            places = listOf("place1", "place2")
        )

        val result = RouteRequestMapper.toResponse(route)

        assertNotNull(result)
        assertEquals(route.id, result.id)
        assertEquals(route.name, result.name)
        assertEquals(route.description, result.description)
        assertEquals(route.places, result.places)
    }
}