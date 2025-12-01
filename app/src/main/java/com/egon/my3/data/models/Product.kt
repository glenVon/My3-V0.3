package com.egon.my3.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class Product(
    @PrimaryKey
    val id: Int,
    val name: String,
    val price: Double,
    val description: String,
    val imageUri: String? = null
)
