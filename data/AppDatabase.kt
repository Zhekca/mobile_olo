package com.example.glitchstore.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        UserEntity::class,
        ProductEntity::class,
        CartEntity::class,
        CatEntity::class,
        CollectionEntity::class,
        ProdImageEntity::class
    ],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun CatDao(): CatDao
    abstract fun CollectionDao(): CollectionDao
    abstract fun userDao(): UserDao

    abstract fun productDao(): ProductDao
    abstract fun cartDao(): CartDao
}