package com.example.glitchstore.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CartDao {

    @Query("SELECT * FROM cart WHERE userid = :userId AND productid = :productId LIMIT 1")
    suspend fun getCartItem(userId: Int, productId: Int): CartEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(cart: CartEntity)

    @Query("UPDATE cart SET count = :count WHERE userid = :userId AND productid = :productId")
    suspend fun updateCount(userId: Int, productId: Int, count: Int)

    @Query("DELETE FROM cart WHERE userid = :userId AND productid = :productId")
    suspend fun delete(userId: Int, productId: Int)

    @Query(
        """
    SELECT 
        p.id AS productId,
        p.name AS name,
        p.price AS price,
        p.discr AS description,
        pi.mainimg AS mainImage,
        c.count AS count
    FROM cart c
    LEFT JOIN products p ON c.productid = p.id
    LEFT JOIN prodimages pi ON p.id = pi.productid
    WHERE c.userid = :userId
"""
    )
    suspend fun getCartItems(userId: Int): List<CartItemWithProduct>
}