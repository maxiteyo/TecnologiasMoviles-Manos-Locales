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
    suspend fun refreshProducts() {
        try {
            // 1. Obtiene los IDs de todos los productos que actualmente son favoritos en la BD local.
            val favoriteProductIds = productDao.getAllProductsList()
                .filter { it.isFavorite }
                .map { it.id }
                .toSet()

            // 2. Llama a la API para obtener la lista de productos más reciente.
            val productsFromApi = apiService.getAllProducts()

            // 3. Fusiona los datos: Mapea la lista de la API y preserva el estado 'isFavorite'.
            val mergedProducts = productsFromApi.map { apiProduct ->
                apiProduct.copy(isFavorite = favoriteProductIds.contains(apiProduct.id))
            }

            // 4. Inserta la nueva lista fusionada en la base de datos.
            // Room reemplazará los productos existentes pero ahora con el estado de favorito correcto.
            productDao.insertAll(mergedProducts)

        } catch (e: Exception) {
            // En caso de un error de red, lo registramos.
            // La app seguirá mostrando los datos que ya tenía en la base de datos.
            Log.e("ProductRepository", "Error al refrescar productos: ${e.message}")
        }
    }

    // Función para actualizar el estado 'isFavorite' de un producto.
    suspend fun updateProduct(product: Product) {
        productDao.update(product)
    }
}