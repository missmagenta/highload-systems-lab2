package itmo.highload

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.reactive.config.EnableWebFlux
import org.springframework.cloud.netflix.hystrix.EnableHystrix
import org.springframework.cloud.openfeign.EnableFeignClients
import reactivefeign.spring.config.EnableReactiveFeignClients

@EnableWebFlux
@EnableFeignClients
@EnableReactiveFeignClients
@EnableHystrix
@SpringBootApplication
class FavoritesServiceApplication
@Suppress("SpreadOperator")
fun main(args: Array<String>) {
    runApplication<FavoritesServiceApplication>(*args)
}
