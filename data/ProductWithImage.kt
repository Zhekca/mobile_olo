package com.example.glitchstore.data

data class ProductWithImage(
    val id: Int,
    val name: String,
    val price: Double,
    val avgRating: Int?,
    val mainImage: String?
)