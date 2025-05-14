package com.example.manoslocales.repositories

import com.example.manoslocales.models.Product

object FavoriteRepository {
    private val favoriteProducts = mutableSetOf<Product>()

    fun addFavorite(product: Product) {
        favoriteProducts.add(product)
    }

    fun removeFavorite(product: Product) {
        favoriteProducts.remove(product)
    }

    fun isFavorite(product: Product): Boolean {
        return favoriteProducts.contains(product)
    }

    fun getFavorites(): List<Product> {
        return favoriteProducts.toList()
    }
}