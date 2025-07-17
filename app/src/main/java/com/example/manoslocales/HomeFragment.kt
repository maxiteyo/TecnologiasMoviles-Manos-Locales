package com.example.manoslocales

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.manoslocales.adapters.ProductAdapter
import com.example.manoslocales.databinding.FragmentHomeBinding
import com.example.manoslocales.ui.ProductViewModel
import com.example.manoslocales.ui.ProductViewModelFactory
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private val viewModel: ProductViewModel by viewModels {
        ProductViewModelFactory((requireActivity().application as ManosLocalesApplication).repository)
    }

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var productAdapter: ProductAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupSpinner()
        setupSearchView()
        setupSwipeRefresh()
        setupBackButton()
        binding.searchView.setIconifiedByDefault(false)
        binding.searchView.isIconified = false

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.filteredProducts.collect { products ->
                    productAdapter.submitList(products)
                }
            }
        }
    }

    private fun setupRecyclerView() {
        productAdapter = ProductAdapter(viewModel) { selectedProduct ->
            val action = HomeFragmentDirections.actionHomeFragmentToProductDetailFragment(
                selectedProduct.id,
                selectedProduct.name,
                selectedProduct.description,
                selectedProduct.category,
                selectedProduct.price.toFloat(),
                selectedProduct.producerName,
                selectedProduct.imageUrl
            )
            findNavController().navigate(action)
        }
        binding.recyclerViewProducts.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewProducts.adapter = productAdapter
    }

    private fun setupSpinner() {
        val categories = resources.getStringArray(R.array.categorias_array)
        val spinnerAdapter = ArrayAdapter(requireContext(), R.layout.spinner, categories).also {
            it.setDropDownViewResource(R.layout.spinner_dropdown)
        }
        binding.spinnerCategory.adapter = spinnerAdapter
        binding.spinnerCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedCategory = parent.getItemAtPosition(position).toString()
                viewModel.setSelectedCategory(if (selectedCategory == getString(R.string.todas)) "Todas" else selectedCategory)
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false
            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.setSearchQuery(newText.orEmpty())
                return true
            }
        })
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.refreshProducts()
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isRefreshing.collect { refreshing ->
                binding.swipeRefreshLayout.isRefreshing = refreshing
            }
        }
    }

    private fun setupBackButton() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    // No hace nada (bloquea "volver")
                }
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}