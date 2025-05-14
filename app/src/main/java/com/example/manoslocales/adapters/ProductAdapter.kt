package com.example.manoslocales.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.manoslocales.R
import com.example.manoslocales.models.Product
import com.example.manoslocales.repositories.FavoriteRepository


class ProductAdapter(
    private var productList: List<Product>,
    private val onItemClick: (Product) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameText: TextView = itemView.findViewById(R.id.textViewProductName)
        val priceText: TextView = itemView.findViewById(R.id.textViewProductPrice)
        val imageView: ImageView = itemView.findViewById(R.id.ImageProduct)
        val favoriteIcon: ImageView = itemView.findViewById(R.id.favoriteIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]
        holder.nameText.text = product.name
        holder.priceText.text = "$${product.price}"
        holder.imageView.setImageResource(product.imageResId)


        holder.itemView.setOnClickListener {
            onItemClick(product)
        }
        holder.favoriteIcon.setImageResource(
            if (FavoriteRepository.isFavorite(product)) R.drawable.ic_favorite else R.drawable.ic_favorite_border
        )

        holder.favoriteIcon.setOnClickListener {
            if (FavoriteRepository.isFavorite(product)) {
                FavoriteRepository.removeFavorite(product)
                holder.favoriteIcon.setImageResource(R.drawable.ic_favorite_border)
            } else {
                FavoriteRepository.addFavorite(product)
                holder.favoriteIcon.setImageResource(R.drawable.ic_favorite)
            }
        }

    }

    fun updateProducts(newProducts: List<Product>) {
        this.productList = newProducts
        notifyDataSetChanged()
    }


    override fun getItemCount(): Int = productList.size
}
