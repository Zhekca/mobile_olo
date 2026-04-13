package com.example.glitchstore.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "collections")
data class CollectionEntity(

    @PrimaryKey
    val id: Int,

    val name: String?, // nullable как в БД

    val img: String? // имя колонки img
)