package com.example.glitchstore.data

import androidx.room.Dao
import androidx.room.Query

@Dao
interface CollectionDao {

    @Query("SELECT * FROM collections")
    suspend fun getAll(): List<CollectionEntity>
}