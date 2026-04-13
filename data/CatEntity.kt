package com.example.glitchstore.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cats")
data class CatEntity(

    @PrimaryKey
    val id: Int,

    val name: String
)