package com.example.manoslocales.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.manoslocales.R
import com.example.manoslocales.databinding.ItemProductBinding
import com.example.manoslocales.models.Product
import com.example.manoslocales.ui.ProductViewModel

class ProductAdapter(
    private val viewModel: ProductViewModel,
    private val onProductClicked: (Product) -> Unit
) : ListAdapter<Product, ProductAdapter.ProductViewHolder>(ProductDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = getItem(position)
        holder.bind(product)
    }

    inner class ProductViewHolder(private val binding: ItemProductBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(product: Product) {
            binding.textViewProductName.text = product.name
            binding.textViewProductPrice.text = "$${product.price}"
            Glide.with(binding.root.context).load(product.imageUrl).into(binding.imageProduct)

            // CORRECCIÓN: Se restaura la lógica para cambiar el ícono usando los nombres que proporcionaste.
            val favoriteIconResource = if (product.isFavorite) {
                R.drawable.ic_favorite // Ícono de estrella llena
            } else {
                R.drawable.ic_favorite_border // Ícono de estrella vacía
            }
            binding.favoriteIcon.setImageResource(favoriteIconResource)

            binding.root.setOnClickListener {
                onProductClicked(product)
            }

            /*binding.favoriteIcon.setOnClickListener {
                val updatedProduct = product.copy(isFavorite = !product.isFavorite)
                viewModel.updateProduct(updatedProduct)
            }*/
            // --- INICIO: CAMBIO CLAVE AQUÍ ---
            // Llama al nuevo método del ViewModel en lugar de actualizar directamente.
            binding.favoriteIcon.setOnClickListener {
                viewModel.toggleFavorite(product)
            }
            // --- FIN: CAMBIO CLAVE AQUÍ ---
        }
    }

    object ProductDiffCallback : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }
}
