package com.egon.my3.data.models

data class CartItem(
    val productId: Int,
    val name: String,
    val price: Double,
    val quantity: Int = 1
)