package com.example.manoslocales.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.manoslocales.models.Product
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(products: List<Product>)

    @Query("SELECT * FROM products")
    fun getAllProducts(): Flow<List<Product>>

    @Query("SELECT * FROM products")
    suspend fun getAllProductsList(): List<Product>

    @Update
    suspend fun update(product: Product)

    @Query("DELETE FROM products WHERE id NOT IN (:ids)")
    suspend fun deleteProductsNotIn(ids: List<String>)

    @Query("DELETE FROM products")
    suspend fun deleteAll()

    @Query("SELECT id FROM products WHERE isFavorite = 1")
    suspend fun getFavoriteProductIds(): List<String>
}