package com.egon.my3.data.repositories

import com.egon.my3.data.database.ProductDao
import com.egon.my3.data.models.Product
import com.egon.my3.data.network.ApiService
import kotlinx.coroutines.flow.Flow

class ProductRepository(private val productDao: ProductDao, private val api: ApiService? = null) {

    fun getAllProductsFlow(): Flow<List<Product>> = productDao.getAllFlow()

    suspend fun getProductById(productId: Int): Product? {
        try {
            api?.let { client ->
                val resp = client.getProductById(productId)
                if (resp != null) return resp
            }
        } catch (_: Exception) {
            // ignore and fallback
        }
        return productDao.getById(productId)
    }

    fun getProductByIdFlow(productId: Int): Flow<Product?> = productDao.getByIdFlow(productId)

    suspend fun addProduct(product: Product) {
        try {
            api?.let { client ->
                val created = client.addProduct(product)
                productDao.insert(created)
                return
            }
        } catch (_: Exception) {
            // fallback
        }
        productDao.insert(product)
    }

    suspend fun updateProduct(updatedProduct: Product) {
        try {
            api?.let { client ->
                val updated = client.updateProduct(updatedProduct.id, updatedProduct)
                productDao.update(updated)
                return
            }
        } catch (_: Exception) {
            // fallback
        }
        productDao.update(updatedProduct)
    }

    suspend fun deleteProduct(productId: Int) {
        try {
            api?.let { client ->
                client.deleteProduct(productId)
            }
        } catch (_: Exception) {
            // ignore
        }
        val product = productDao.getById(productId)
        if (product != null) {
            productDao.delete(product)
        }
    }

    suspend fun getNextProductId(): Int {
        val maxId = productDao.getMaxId() ?: 0
        return maxId + 1
    }
}
