package com.egon.userservice.model

data class User(
    var id: Long? = null,
    val name: String,
    val email: String,
    val password: String,
    val isAdmin: Boolean = false
)
