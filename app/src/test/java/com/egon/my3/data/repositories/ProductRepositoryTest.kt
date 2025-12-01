package com.egon.my3.data.repositories

import com.egon.my3.data.models.Product
import com.egon.my3.data.network.ApiService
import com.egon.my3.data.database.ProductDao
import kotlinx.coroutines.flow.flowOf
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class ProductRepositoryTest {

    private lateinit var apiService: ApiService
    private lateinit var productDao: ProductDao
    private lateinit var productRepository: ProductRepository

    @Before
    fun setUp() {
        apiService = mockk()
        productDao = mockk()
        productRepository = ProductRepository(productDao, apiService)
    }

    @Test
    fun `getAllProductsFlow returns products from apiService`() = runTest {
        // Arrange
        val products = listOf(
            Product(1, "Laptop", 999.99, "A great laptop"),
            Product(2, "Smartphone", 499.99, "A great phone")
        )
        coEvery { productDao.getAllFlow() } returns flowOf(products)

        // Act
        val result = productRepository.getAllProductsFlow().first()

        // Assert
        assertEquals(products, result)
    }

    @Test
    fun `getProductById returns correct product from apiService`() = runTest {
        // Arrange
        val productId = 1
        val product = Product(productId, "Laptop", 999.99, "A great laptop")
        // CORRECT MOCK: We now mock the actual function being called.
        coEvery { productDao.getById(productId) } returns product

        // Act
        val result = productRepository.getProductById(productId)

        // Assert
        assertEquals(product, result)
    }

    @Test
    fun `getProductById returns null if product not found in apiService response`() = runTest {
        // Arrange
        val productId = 3 // This ID does not exist
        // CORRECT MOCK: We mock the call for this specific ID to return null.
        coEvery { productDao.getById(productId) } returns null

        // Act
        val result = productRepository.getProductById(productId)

        // Assert
        assertNull(result)
    }
}
