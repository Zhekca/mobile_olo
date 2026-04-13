package com.example.glitchstore.data

data class CartItemWithProduct(
    val productId: Int,
    val name: String,
    val price: Double,
    val description: String,
    val mainImage: String?,
    val count: Int
)