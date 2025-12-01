package com.egon.productservice.repository

import com.egon.productservice.model.Product
import org.springframework.stereotype.Repository
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

@Repository
class ProductRepository {
    private val data = ConcurrentHashMap<Long, Product>()
    private val idSequence = AtomicLong(1)

    init {
        val p1 = Product(id = idSequence.getAndIncrement(), name = "Laptop Gaming", description = "Laptop potente para gaming", price = 999.99, userId = 1L)
        val p2 = Product(id = idSequence.getAndIncrement(), name = "Smartphone", description = "Teléfono inteligente", price = 499.99, userId = 1L)
        data[p1.id!!] = p1
        data[p2.id!!] = p2
    }

    fun findAll(): List<Product> = data.values.toList()

    fun findById(id: Long): Product? = data[id]

    fun save(product: Product): Product {
        val newId = product.id ?: idSequence.getAndIncrement()
        val newProduct = product.copy(id = newId)
        data[newId] = newProduct
        return newProduct
    }

    fun delete(id: Long) {
        data.remove(id)
    }
}
