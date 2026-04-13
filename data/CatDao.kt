package com.example.glitchstore.data

import androidx.room.Dao
import androidx.room.Query

@Dao
interface CatDao {

    @Query("SELECT * FROM cats")
    suspend fun getAll(): List<CatEntity>
}