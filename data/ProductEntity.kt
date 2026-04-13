package com.example.glitchstore.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "products",
    foreignKeys = [
        ForeignKey(
            entity = CatEntity::class,
            parentColumns = ["id"],
            childColumns = ["catid"]
        ),
        ForeignKey(
            entity = CollectionEntity::class,
            parentColumns = ["id"],
            childColumns = ["collectionid"]
        )
    ]
)
data class ProductEntity(

    @PrimaryKey
    val id: Int,

    @ColumnInfo(name = "origname")
    val origName: Int?,

    val name: String,

    @ColumnInfo(name = "discr")
    val discr: String,

    val price: Double,

    val material: String,

    @ColumnInfo(name = "rewscount")
    val revsCount: Int?,

    @ColumnInfo(name = "avgrating")
    val avgRating: Int?,

    @ColumnInfo(name = "collectionid")
    val collectionId: Int,

    @ColumnInfo(name = "catid")
    val catId: Int
)