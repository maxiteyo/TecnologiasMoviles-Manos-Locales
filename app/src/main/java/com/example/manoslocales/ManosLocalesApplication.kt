package com.example.manoslocales

import android.app.Application
import com.example.manoslocales.data.AppDatabase
import com.example.manoslocales.data.ProductRepository
import com.example.manoslocales.network.ApiClient // Importa tu ApiClient

class ManosLocalesApplication : Application() {

    // 1. Base de datos local
    private val database by lazy { AppDatabase.getDatabase(this) }

    // 2. Cliente de API (Retrofit) usando el objeto ApiClient que ya creaste.
    private val apiService by lazy { ApiClient.create() }

    // 3. Repositorio que ahora recibe tanto el DAO como el servicio de API
    val repository by lazy { ProductRepository(database.productDao(), apiService) }
}