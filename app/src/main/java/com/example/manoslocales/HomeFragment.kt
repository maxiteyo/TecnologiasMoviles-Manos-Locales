package com.example.manoslocales

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SearchView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
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
        setupBackButton()

        // Observa la nueva lista FILTRADA del ViewModel
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                // ¡CAMBIO CLAVE! Observamos 'filteredProducts'
                viewModel.filteredProducts.collect { products ->
                    // Simplemente enviamos la lista al adaptador.
                    productAdapter.submitList(products)
                }
            }
        }
    }

    private fun setupRecyclerView() {
        // Pasamos el viewModel al adaptador como antes
        productAdapter = ProductAdapter(viewModel) { selectedProduct ->
            // Tu lógica de navegación no cambia
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
                // ¡CAMBIO CLAVE! Informamos al ViewModel de la nueva categoría
                val selectedCategory = parent.getItemAtPosition(position).toString()
                // Usamos "Todas" si es la primera opción, que coincide con el valor por defecto en el ViewModel
                viewModel.setSelectedCategory(if (selectedCategory == getString(R.string.todas)) "Todas" else selectedCategory)
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false
            override fun onQueryTextChange(newText: String?): Boolean {
                // ¡CAMBIO CLAVE! Informamos al ViewModel de la nueva búsqueda
                viewModel.setSearchQuery(newText.orEmpty())
                return true
            }
        })
    }

    // Ya no necesitas la función filterProducts() ni la variable fullProductList

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