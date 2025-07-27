package com.example.manoslocales.network

import com.example.manoslocales.models.Product
import retrofit2.http.GET

interface ProductApiService {

    @GET("my-products")
    suspend fun getAllProducts(): List<Product>
}