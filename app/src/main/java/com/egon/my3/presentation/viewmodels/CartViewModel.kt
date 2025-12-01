package com.egon.my3.presentation.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.egon.my3.data.models.CartItem
import com.egon.my3.data.models.Product

class CartViewModel : ViewModel() {
    var cartItems: List<CartItem> by mutableStateOf(emptyList())
        private set

    val cartItemCount: Int get() = cartItems.sumOf { it.quantity }
    val cartTotal: Double get() = cartItems.sumOf { it.price * it.quantity }

    fun addToCart(product: Product) {
        val currentItems = cartItems.toMutableList()
        val existingItem = currentItems.find { it.productId == product.id }

        if (existingItem != null) {
            val updatedItems = currentItems.map { item ->
                if (item.productId == product.id) {
                    item.copy(quantity = item.quantity + 1)
                } else {
                    item
                }
            }
            cartItems = updatedItems
        } else {
            currentItems.add(
                CartItem(
                    productId = product.id,
                    name = product.name,
                    price = product.price,
                    quantity = 1
                )
            )
            cartItems = currentItems
        }
    }

    fun removeFromCart(productId: Int) {
        cartItems = cartItems.filter { it.productId != productId }
    }

    fun updateQuantity(productId: Int, quantity: Int) {
        if (quantity <= 0) {
            removeFromCart(productId)
        } else {
            cartItems = cartItems.map { item ->
                if (item.productId == productId) {
                    item.copy(quantity = quantity)
                } else {
                    item
                }
            }
        }
    }

    fun clearCart() {
        cartItems = emptyList()
    }
}
