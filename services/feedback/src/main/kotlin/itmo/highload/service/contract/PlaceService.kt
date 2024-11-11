package itmo.highload.service.contract

import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import reactivefeign.spring.config.ReactiveFeignClient
import reactor.core.publisher.Mono

@ReactiveFeignClient(
    value = "place-service",
    url = "http://localhost:8086/api/v1",
    fallback = PlaceServiceFallback::class
)
interface PlaceService {
    @GetMapping("/place/{id}")
    fun getPlace(@PathVariable id: String, @RequestHeader("Authorization") token: String): Mono<Int>
}

@Component
class PlaceServiceFallback : PlaceService {
    override fun getPlace(@PathVariable id: String, @RequestHeader("Authorization") token: String): Mono<Int> {
        return Mono.empty()
    }
}
