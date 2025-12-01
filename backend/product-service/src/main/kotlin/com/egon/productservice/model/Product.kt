package com.egon.productservice.model

data class Product(
    val id: Long? = null,
    val name: String,
    val description: String,
    val price: Double,
    val userId: Long? = null
)
