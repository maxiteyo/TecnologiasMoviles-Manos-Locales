package com.example.manoslocales.data

import android.util.Log
import com.example.manoslocales.models.Product
import com.example.manoslocales.network.ProductApiService
import kotlinx.coroutines.flow.Flow

class ProductRepository(
    private val productDao: ProductDao,
    private val apiService: ProductApiService
) {

    // 1. Expone la lista de productos directamente desde la base de datos (Room).
    val allProducts: Flow<List<Product>> = productDao.getAllProducts()

    // 2. Función para actualizar la base de datos con datos de la red.

    suspend fun refreshProducts(favoriteProductIds: Set<String>) {
        try {
            val productsFromApi = apiService.getAllProducts()

            val mergedProducts = productsFromApi.map { apiProduct ->
                apiProduct.copy(isFavorite = favoriteProductIds.contains(apiProduct.id))
            }

            productDao.deleteAll()
            productDao.insertAll(mergedProducts)

        } catch (e: Exception) {
            Log.e("ProductRepository", "Error al refrescar productos: ${e.message}", e)
        }
    }

    // Función para actualizar el estado 'isFavorite' de un producto.
    suspend fun updateProduct(product: Product) {
        productDao.update(product)
    }
}