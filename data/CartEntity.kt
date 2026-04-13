package com.example.glitchstore.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart")
data class CartEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val userid: Int,

    val productid: Int,

    val count: Int
)