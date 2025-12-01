package com.egon.my3.presentation.viewmodels

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.egon.my3.data.models.Product
import com.egon.my3.data.repositories.ProductRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProductViewModel(private val productRepository: ProductRepository) : ViewModel() {
    private val TAG = "ProductViewModel"

    val allProducts: StateFlow<List<Product>> = productRepository
        .getAllProductsFlow()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun getProductById(productId: Int): Product? = allProducts.value.find { it.id == productId }

    fun addProduct(name: String, price: Double, description: String, imageUri: Uri?, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                Log.d(TAG, "addProduct -> start name=$name price=$price")
                val id = productRepository.getNextProductId()
                val newProduct = Product(
                    id = id,
                    name = name,
                    price = price,
                    description = description,
                    imageUri = imageUri?.toString()
                )
                productRepository.addProduct(newProduct)
                Log.d(TAG, "Added product: $newProduct")
                onComplete()
            } catch (t: Throwable) {
                Log.e(TAG, "Error adding product", t)
                onComplete()
            }
        }
    }

    fun updateProduct(productId: Int, name: String, price: Double, description: String, imageUri: Uri?, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                Log.d(TAG, "updateProduct -> start id=$productId name=$name price=$price")
                val updatedProduct = Product(
                    id = productId,
                    name = name,
                    price = price,
                    description = description,
                    imageUri = imageUri?.toString()
                )
                productRepository.updateProduct(updatedProduct)
                Log.d(TAG, "Updated product: $updatedProduct")
                onComplete()
            } catch (t: Throwable) {
                Log.e(TAG, "Error updating product", t)
                onComplete()
            }
        }
    }

    fun deleteProduct(productId: Int, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                Log.d(TAG, "deleteProduct -> start id=$productId")
                productRepository.deleteProduct(productId)
                Log.d(TAG, "Deleted product id: $productId")
                onComplete()
            } catch (t: Throwable) {
                Log.e(TAG, "Error deleting product", t)
                onComplete()
            }
        }
    }
}
