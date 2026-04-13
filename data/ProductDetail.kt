package com.example.glitchstore.data

// --- START: ProductFullDetail ---
data class ProductDetail(
    val id: Int,
    val name: String,
    val discr: String,
    val price: Double,
    val material: String,
    val mainImage: String?,
    val collectionName: String?
)