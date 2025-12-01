package com.egon.productservice.client

import com.egon.productservice.model.User
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Component
class UserClient(private val webClient: WebClient) {

    fun getUserById(userId: Long): Mono<User> {
        return webClient.get()
            .uri("/users/{userId}", userId)
            .retrieve()
            .bodyToMono(User::class.java)
    }
}
