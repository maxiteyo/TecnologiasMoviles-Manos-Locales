package com.example.manoslocales.network

import com.example.manoslocales.models.Product
import retrofit2.http.GET
import retrofit2.http.Query

interface ProductApiService {


    @GET("my-products")
    suspend fun getProducts(): List<Product>

    // --- MÉTODO NUEVO PARA BÚSQUEDA ---
    // Este método buscará productos. La URL final será algo como:
    // http://.../products?q=termino_buscado
    @GET("my-products")
    suspend fun searchProducts(@Query("q") searchTerm: String): List<Product>

    @GET("my-products")
    suspend fun getAllProducts(): List<Product>
}