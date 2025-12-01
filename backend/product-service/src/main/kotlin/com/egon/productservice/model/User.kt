package com.egon.productservice.model

data class User(
    val id: Long = 0,
    val name: String = "",
    val email: String = "",
    val isAdmin: Boolean = false
)
