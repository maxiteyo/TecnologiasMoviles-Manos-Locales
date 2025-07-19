package com.example.manoslocales.models

import com.google.gson.annotations.JsonAdapter
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName


@Entity(tableName = "products")
data class Product(
    @PrimaryKey
    @SerializedName("id")
    @JsonAdapter(ToStringAdapter::class)
    val id: String, // Le damos un valor por defecto para que Room pueda autogenerarlo
    val name: String,
    val description: String,
    val category: String,
    val price: Double,
    @SerializedName("producerName") val producerName: String,
    // CAMBIO: De Int a String para la URL de la imagen
    @SerializedName("imageResId") val imageUrl: String,
    var isFavorite: Boolean = false
){
    // Constructor vacío necesario para la deserialización de Firestore
    constructor() : this("", "", "", "", 0.0, "", "", false)
}