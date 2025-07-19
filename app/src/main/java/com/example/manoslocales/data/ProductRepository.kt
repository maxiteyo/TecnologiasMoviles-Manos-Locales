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
    // Room se encargará de actualizar este Flow automáticamente cada vez que los
    // datos de la tabla 'products' cambien.
    val allProducts: Flow<List<Product>> = productDao.getAllProducts()

    // 2. Función para actualizar la base de datos con datos de la red.
    // Es 'suspend' porque realiza operaciones de red y de base de datos.
    /*suspend fun refreshProducts() {
        try {
            val favoriteProductIds = productDao.getAllProductsList()
                .filter { it.isFavorite }
                .map { it.id }
                .toSet()

            val productsFromApi = apiService.getAllProducts()
            Log.d("ProductRepository", "Productos recibidos de la API: ${productsFromApi.size}")

            val mergedProducts = productsFromApi.map { apiProduct ->
                apiProduct.copy(isFavorite = favoriteProductIds.contains(apiProduct.id))
            }

            val apiIds = mergedProducts.map { it.id }
            productDao.deleteProductsNotIn(apiIds) // Elimina los que ya no están en la API
            productDao.insertAll(mergedProducts)   // Inserta/actualiza los nuevos y existentes

            Log.d("ProductRepository", "Productos sincronizados en Room: ${mergedProducts.size}")

        } catch (e: Exception) {
            Log.e("ProductRepository", "Error al refrescar productos: ${e.message}", e)
        }
    }*/

    suspend fun refreshProducts(favoriteProductIds: Set<String>) {
        try {
            // 1. Obtener los productos desde la API.
            val productsFromApi = apiService.getAllProducts()

            // 2. Fusionar las listas usando los IDs de favoritos de Firebase.
            val mergedProducts = productsFromApi.map { apiProduct ->
                apiProduct.copy(isFavorite = favoriteProductIds.contains(apiProduct.id))
            }

            // 3. Limpiar la base de datos e insertar la lista fusionada.
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