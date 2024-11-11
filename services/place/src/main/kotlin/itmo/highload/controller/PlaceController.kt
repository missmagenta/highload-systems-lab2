package itmo.highload.controller

import itmo.highload.api.dto.PlaceDto
import itmo.highload.api.dto.UpdatePlaceDescriptionDto
import itmo.highload.api.dto.UpdatePlaceNameDto
import itmo.highload.api.dto.response.PlaceResponse
import itmo.highload.model.PlaceMapper
import itmo.highload.security.jwt.JwtUtils
import itmo.highload.service.PlaceService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("\${app.base-url}/place")
class PlaceController(val placeService: PlaceService, private val jwtUtils: JwtUtils) {
    @GetMapping
    @PreAuthorize("hasAnyAuthority('OWNER', 'USER')")
    fun getAllPlaces(): Flux<PlaceResponse> = placeService.getAllPlaces().map { PlaceMapper.toPlaceResponse(it) }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('OWNER', 'USER')")
    fun getPlace(@PathVariable id: String): Mono<PlaceResponse> {
        return placeService.getPlace(id).map { PlaceMapper.toPlaceResponse(it) }
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('OWNER')")
    fun addPlace(
        @RequestBody @Valid request: PlaceDto,
        @RequestHeader("Authorization") token: String
    ): Mono<PlaceResponse> {
        val ownerId = jwtUtils.extractUserId(token)
        return placeService.addPlace(ownerId, request).map { PlaceMapper.toPlaceResponse(it) }
    }

    @PatchMapping("/{id}/name")
    @PreAuthorize("hasAuthority('OWNER')")
    fun updateName(
        @PathVariable id: String,
        @RequestBody @Valid request: UpdatePlaceNameDto,
        @RequestHeader("Authorization") token: String
    ): Mono<PlaceResponse> {
        val ownerId = jwtUtils.extractUserId(token)
        return placeService.updateName(ownerId, id, request.name).map { PlaceMapper.toPlaceResponse(it) }
    }

    @PatchMapping("/{id}/description")
    @PreAuthorize("hasAuthority('OWNER')")
    fun updateDescription(
        @PathVariable id: String,
        @RequestBody @Valid request: UpdatePlaceDescriptionDto,
        @RequestHeader("Authorization") token: String
    ): Mono<PlaceResponse> {
        val ownerId = jwtUtils.extractUserId(token)
        return placeService.updateDescription(ownerId, id, request.description).map { PlaceMapper.toPlaceResponse(it) }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('OWNER')")
    fun deletePlace(
        @PathVariable id: String,
        @RequestHeader("Authorization") token: String
    ): Mono<Void> {
        val ownerId = jwtUtils.extractUserId(token)
        return placeService.deletePlace(id, ownerId)
    }

    // ADD OTHER METHODS
}
