package com.example.manoslocales.models

data class Product(
    val id: Int,
    val name: String,
    val description: String,
    val category: String,
    val price: Double,
    val producerName: String,
    val imageResId: Int
)