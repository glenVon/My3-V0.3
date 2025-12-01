package com.egon.productservice.controller

import com.egon.productservice.client.UserClient
import com.egon.productservice.dto.ProductDto
import com.egon.productservice.model.Product
import com.egon.productservice.repository.ProductRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/products")
class ProductController(
    private val repo: ProductRepository,
    private val userClient: UserClient
) {

    @GetMapping
    fun all(): Flux<ProductDto> {
        return Flux.fromIterable(repo.findAll())
            .flatMap { product ->
                val userMono = product.userId?.let { userClient.getUserById(it) } ?: Mono.empty()
                userMono.map { user ->
                    ProductDto(
                        id = product.id!!,
                        name = product.name,
                        description = product.description,
                        price = product.price,
                        user = user
                    )
                }.defaultIfEmpty(ProductDto(
                    id = product.id!!,
                    name = product.name,
                    description = product.description,
                    price = product.price,
                    user = null
                ))
            }
    }

    @GetMapping("/{id}")
    fun byId(@PathVariable id: Long): Mono<ResponseEntity<ProductDto>> {
        val product = repo.findById(id)
        return if (product == null) {
            Mono.just(ResponseEntity.notFound().build())
        } else {
            val userMono = product.userId?.let { userClient.getUserById(it) } ?: Mono.empty()
            userMono.map { user ->
                ProductDto(
                    id = product.id!!,
                    name = product.name,
                    description = product.description,
                    price = product.price,
                    user = user
                )
            }.defaultIfEmpty(ProductDto(
                id = product.id!!,
                name = product.name,
                description = product.description,
                price = product.price,
                user = null
            )).map { ResponseEntity.ok(it) }
        }
    }

    @PostMapping
    fun create(@RequestBody product: Product): ResponseEntity<Product> = ResponseEntity.ok(repo.save(product))

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> {
        repo.delete(id)
        return ResponseEntity.noContent().build()
    }
}
