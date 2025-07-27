package com.example.manoslocales.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.manoslocales.data.ProductRepository
import com.example.manoslocales.models.Product
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


class ProductViewModel(private val repository: ProductRepository) : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private val _searchQuery = MutableStateFlow("")

    private val _selectedCategory = MutableStateFlow("Todas")

    private val _productsState = MutableStateFlow<List<Product>>(emptyList())

    val filteredProducts: StateFlow<List<Product>> =
        combine(_productsState, _searchQuery, _selectedCategory) { products, query, category ->
            products.filter { product ->
                val matchesCategory = category == "Todas" || product.category.equals(category, ignoreCase = true)
                val matchesSearch = product.name.lowercase().contains(query.lowercase())
                matchesCategory && matchesSearch
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        viewModelScope.launch {
            repository.allProducts.collect { productsFromDb ->
                _productsState.value = productsFromDb
            }
        }
        refreshProducts()
    }

    fun toggleFavorite(product: Product) {
        val userId = auth.currentUser?.uid ?: return
        val newFavoriteState = !product.isFavorite

        _productsState.update { currentProducts ->
            currentProducts.map {
                if (it.id == product.id) it.copy(isFavorite = newFavoriteState) else it
            }
        }

        viewModelScope.launch(Dispatchers.IO) {
            val favoriteRef = db.collection("users").document(userId)
                .collection("favorites").document(product.id)
            try {
                if (newFavoriteState) {
                    favoriteRef.set(mapOf("addedAt" to System.currentTimeMillis())).await()
                    Log.d("ProductViewModel", "Producto ${product.id} añadido a favoritos en Firebase.")
                } else {
                    favoriteRef.delete().await()
                    Log.d("ProductViewModel", "Producto ${product.id} eliminado de favoritos en Firebase.")
                }

                val updatedProduct = product.copy(isFavorite = newFavoriteState)
                repository.updateProduct(updatedProduct)

            } catch (e: Exception) {
                Log.e("ProductViewModel", "ERROR en Firebase al actualizar favorito para ${product.id}", e)
                _productsState.update { currentProducts ->
                    currentProducts.map {
                        if (it.id == product.id) it.copy(isFavorite = product.isFavorite) // Revertir
                        else it
                    }
                }
            }
        }
    }

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    fun refreshProducts() {
        viewModelScope.launch(Dispatchers.IO) {
            _isRefreshing.value = true
            try {
                val favoriteIds = getFavoriteProductIdsFromFirestore()
                repository.refreshProducts(favoriteIds)
            } catch (e: Exception) {
                Log.e("ProductViewModel", "Error al refrescar productos", e)
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedCategory(category: String) {
        _selectedCategory.value = category
    }

    private suspend fun getFavoriteProductIdsFromFirestore(): Set<String> {
        val userId = auth.currentUser?.uid ?: return emptySet()
        return try {
            val snapshot = db.collection("users").document(userId)
                .collection("favorites")
                .get()
                .await()
            snapshot.documents.map { it.id }.toSet()
        } catch (e: Exception) {
            Log.e("ProductViewModel", "Error al obtener favoritos de Firestore", e)
            emptySet()
        }
    }
}

class ProductViewModelFactory(private val repository: ProductRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProductViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProductViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}