package com.egon.productservice.dto

import com.egon.productservice.model.User

data class ProductDto(
    val id: Long,
    val name: String,
    val description: String,
    val price: Double,
    val user: User?
)
