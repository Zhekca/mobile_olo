package com.example.glitchstore.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "prodimages",
    foreignKeys = [
        ForeignKey(
            entity = ProductEntity::class,
            parentColumns = ["id"],
            childColumns = ["productid"]
        )
    ]
)
data class ProdImageEntity(

    @PrimaryKey
    val id: Int,

    @ColumnInfo(name = "productid")
    val productId: Int,

    @ColumnInfo(name = "mainimg")
    val mainImage: String?,

    val images: String?
)