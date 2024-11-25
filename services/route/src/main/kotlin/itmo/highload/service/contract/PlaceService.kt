package itmo.highload.service.contract

import itmo.highload.api.dto.response.PlaceResponse
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import reactivefeign.spring.config.ReactiveFeignClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@ReactiveFeignClient(
    value = "place-service",
    url = "http://place:8086/api/v1",
    fallback = PlaceServiceFallback::class
)
interface PlaceService {
    @GetMapping("/place/{id}")
    fun getPlace(@PathVariable("id") id: String, @RequestHeader("Authorization") token: String): Mono<PlaceResponse>
}

@Component
class PlaceServiceFallback : PlaceService {
    override fun getPlace(id: String, token: String): Mono<PlaceResponse> {
        return Mono.empty()
    }
}
