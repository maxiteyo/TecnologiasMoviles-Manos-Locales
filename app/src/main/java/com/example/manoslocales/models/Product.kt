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
    val id: String,
    val name: String,
    val description: String,
    val category: String,
    val price: Double,
    @SerializedName("producerName") val producerName: String,
    @SerializedName("imageResId") val imageUrl: String,
    var isFavorite: Boolean = false
){
    constructor() : this("", "", "", "", 0.0, "", "", false)
}