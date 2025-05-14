package com.example.manoslocales.models

data class Product(
    val id: Int,
    val name: String,
    val description: String,
    val category: String,
    val price: Double,
    val producerName: String,
    val imageResId: Int,
    var isFavorite: Boolean = false
){
    override fun equals(other: Any?): Boolean {
        return (other is Product) && this.id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}