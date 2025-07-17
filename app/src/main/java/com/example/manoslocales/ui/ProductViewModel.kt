package com.example.manoslocales.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.manoslocales.data.ProductRepository
import com.example.manoslocales.models.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProductViewModel(private val repository: ProductRepository) : ViewModel() {

    // --- ESTADO INTERNO PARA FILTROS ---
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("Todas") // "Todas" como valor inicial
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    // --- FLOW COMBINADO Y FILTRADO ---
    // Este es el Flow que la UI observará. Combina los productos de la BD
    // con los filtros actuales y emite una lista filtrada.
    val filteredProducts: StateFlow<List<Product>> =
        combine(repository.allProducts, _searchQuery, _selectedCategory) { products, query, category ->
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

    // --- MÉTODOS PARA ACTUALIZAR FILTROS ---
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedCategory(category: String) {
        _selectedCategory.value = category
    }

    // --- MÉTODOS EXISTENTES (SIN CAMBIOS) ---
    /*init {
        refreshProducts()
    }*/

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    fun refreshProducts() {
        viewModelScope.launch {
            _isRefreshing.value = true
            repository.refreshProducts()
            _isRefreshing.value = false
        }
    }

    fun updateProduct(product: Product) {
        viewModelScope.launch {
            repository.updateProduct(product)
        }
    }
}

// El Factory no cambia
class ProductViewModelFactory(private val repository: ProductRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProductViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProductViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}