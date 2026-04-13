package com.example.glitchstore.data

import androidx.room.Dao
import androidx.room.Query

@Dao
interface ProductDao {

    @Query(
        """
    SELECT 
        p.id AS id,
        p.name AS name,
        p.price AS price,
        p.avgrating AS avgRating,
        pi.mainimg AS mainImage
    FROM products p
    LEFT JOIN prodimages pi 
    ON p.id = pi.productid
"""
    )
    suspend fun getProductsWithImages(): List<ProductWithImage>

    @Query("SELECT * FROM products WHERE id = :id LIMIT 1")
    suspend fun getProductById(id: Int): ProductEntity

    @Query(
        """
        SELECT 
            p.id AS id,
            p.name AS name,
            p.discr AS discr,
            p.price AS price,
            p.material AS material,
            pi.mainimg AS mainImage,
            c.name AS collectionName
        FROM products p
        LEFT JOIN prodimages pi 
            ON p.id = pi.productid
        LEFT JOIN collections c 
            ON p.collectionid = c.id
        WHERE p.id = :id
        LIMIT 1
    """
    )
    suspend fun getProductDetail(id: Int): ProductDetail

    @Query(
        """
    SELECT images 
    FROM prodimages 
    WHERE productid = :productId 
    LIMIT 1
"""
    )
    suspend fun getProductImages(productId: Int): String?

    @Query(
        """
    SELECT 
        p.id AS id,
        p.name AS name,
        p.price AS price,
        p.avgrating AS avgRating,
        pi.mainimg AS mainImage
    FROM products p
    LEFT JOIN prodimages pi 
        ON p.id = pi.productid
    WHERE (:catId IS NULL OR p.catid = :catId)
      AND (:collectionId IS NULL OR p.collectionid = :collectionId)
"""
    )
    suspend fun getFilteredProductsWithImages(
        catId: Int?,
        collectionId: Int?
    ): List<ProductWithImage>
}