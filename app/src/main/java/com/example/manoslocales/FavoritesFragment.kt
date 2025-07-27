package com.example.manoslocales

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.manoslocales.adapters.ProductAdapter
import com.example.manoslocales.databinding.FragmentFavoritesBinding
import com.example.manoslocales.models.Product
import com.example.manoslocales.ui.ProductViewModel
import com.example.manoslocales.ui.ProductViewModelFactory
import kotlinx.coroutines.launch

class FavoritesFragment : Fragment() {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProductViewModel by viewModels {
        ProductViewModelFactory((requireActivity().application as ManosLocalesApplication).repository)
    }

    private lateinit var productAdapter: ProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.filteredProducts.collect { allProducts ->
                    val favoriteProducts = allProducts.filter { it.isFavorite }
                    productAdapter.submitList(favoriteProducts)
                }
            }
        }
    }

    private fun setupRecyclerView() {
        productAdapter = ProductAdapter(viewModel) { selectedProduct ->
            navigateToDetail(selectedProduct)
        }

        binding.recyclerViewProducts.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewProducts.adapter = productAdapter
    }

    private fun navigateToDetail(product: Product) {
        val action = FavoritesFragmentDirections.actionFavoritesFragmentToProductDetailFragment(
            product.id,
            product.name,
            product.description,
            product.category,
            product.price.toFloat(),
            product.producerName,
            product.imageUrl
        )
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}