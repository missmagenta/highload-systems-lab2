package itmo.highload.controller

import itmo.highload.api.dto.CreateRouteRequest
import itmo.highload.api.dto.response.RouteResponse
import itmo.highload.model.RouteRequestMapper
import itmo.highload.security.jwt.JwtUtils
import itmo.highload.service.RouteService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("\${app.base-url}/route")
class RouteController(
    private val routeService: RouteService, private val jwtUtils: JwtUtils
) {

    @GetMapping
    @PreAuthorize("hasAnyAuthority('OWNER', 'USER')")
    fun getAllRoutes(): Flux<RouteResponse> {
        return routeService.getAllRoutes().map { RouteRequestMapper.toResponse(it) }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('OWNER', 'USER')")
    fun getRoute(@PathVariable id: String): Mono<RouteResponse> {
        return routeService.getRoute(id).map { RouteRequestMapper.toResponse(it) }
    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyAuthority('OWNER', 'USER')")
    fun createRoute(
        @RequestBody @Valid request: CreateRouteRequest, @RequestHeader("Authorization") token: String
    ): Mono<RouteResponse> {
        val userId = jwtUtils.extractUserId(token)
        return routeService.createRoute(request, userId).map { RouteRequestMapper.toResponse(it) }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyAuthority('OWNER', 'USER')")
    fun deleteRoute(
        @PathVariable id: String, @RequestHeader("Authorization") token: String
    ): Mono<Void> {
        val userId = jwtUtils.extractUserId(token)
        return routeService.deleteRoute(id, userId)
    }

    @GetMapping("/by-place/{id}")
    fun getRoutesByPlaceContains(
        @PathVariable id: String,
    ): Flux<RouteResponse> {
        return routeService.findRoutesByPlaceContains(id).map { RouteRequestMapper.toResponse(it) }
    }
}
