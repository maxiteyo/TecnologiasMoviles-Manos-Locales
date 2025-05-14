package com.example.manoslocales

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.manoslocales.adapters.ProductAdapter
import com.example.manoslocales.repositories.FavoriteRepository

class FavoritesFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_favorites, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewProducts)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = ProductAdapter(FavoriteRepository.getFavorites()) { selectedProduct ->
            val action = FavoritesFragmentDirections.actionFavoritesFragmentToProductDetailFragment(
                selectedProduct.id,
                selectedProduct.name,
                selectedProduct.description,
                selectedProduct.category,
                selectedProduct.price.toFloat(),
                selectedProduct.producerName,
                selectedProduct.imageResId
            )
            findNavController().navigate(action)
        }

        recyclerView.adapter = adapter

        return view
    }
    override fun onResume() {
        super.onResume()
        adapter.updateProducts(FavoriteRepository.getFavorites())
    }

}
