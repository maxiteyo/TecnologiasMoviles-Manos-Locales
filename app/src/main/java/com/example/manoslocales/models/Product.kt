package com.example.manoslocales.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "products")
data class Product(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0, // Le damos un valor por defecto para que Room pueda autogenerarlo
    val name: String,
    val description: String,
    val category: String,
    val price: Double,
    @SerializedName("producerName") val producerName: String,
    // CAMBIO: De Int a String para la URL de la imagen
    @SerializedName("imageResId") val imageUrl: String,
    var isFavorite: Boolean = false
)