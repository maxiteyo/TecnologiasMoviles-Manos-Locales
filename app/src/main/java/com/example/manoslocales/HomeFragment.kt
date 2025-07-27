package com.example.manoslocales

import android.Manifest
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
import com.example.manoslocales.adapters.ProductAdapter
import com.example.manoslocales.databinding.FragmentHomeBinding
import com.example.manoslocales.ui.ProductViewModel
import com.example.manoslocales.ui.ProductViewModelFactory
import kotlinx.coroutines.launch
import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import kotlin.toString

class HomeFragment : Fragment() {

    private val viewModel: ProductViewModel by viewModels {
        ProductViewModelFactory((requireActivity().application as ManosLocalesApplication).repository)
    }

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var productAdapter: ProductAdapter

    private val speechRecognizerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val spokenText = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.get(0)

            if (!spokenText.isNullOrEmpty()) {
                binding.searchView.setQuery(spokenText, true)
            }
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            launchVoiceSearch()
        } else {
            Toast.makeText(requireContext(), "Permiso de micrófono necesario para la búsqueda por voz.", Toast.LENGTH_SHORT).show()
        }
    }

    private val requestNotificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Toast.makeText(requireContext(), "Gracias. Las notificaciones están activadas.", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "No se podrán mostrar notificaciones.", Toast.LENGTH_SHORT).show()
        }
    }

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
        setupVoiceSearch()
        setupNotificationPermission()
        binding.searchView.setIconifiedByDefault(false)
        binding.searchView.isIconified = false
        binding.searchView.clearFocus()
        binding.root.requestFocus()

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.filteredProducts.collect { products ->
                    productAdapter.submitList(products)
                }
            }
        }
    }

    // FUNCIÓN para manejar el permiso de notificaciones ---
    private fun setupNotificationPermission() {
        val sharedPreferences = requireActivity().getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        val areNotificationsEnabledInSettings = sharedPreferences.getBoolean("notifications_enabled", true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && areNotificationsEnabledInSettings) {
            when {
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                }
                else -> {
                    requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }
    }

    private fun setupVoiceSearch() {
        binding.voiceSearchButton.setOnClickListener {
            val sharedPreferences = requireActivity().getSharedPreferences("app_settings", Context.MODE_PRIVATE)
            val isVoiceSearchEnabledInSettings = sharedPreferences.getBoolean("voice_search_enabled", true)

            if (isVoiceSearchEnabledInSettings) {
                when {
                    ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.RECORD_AUDIO
                    ) == android.content.pm.PackageManager.PERMISSION_GRANTED -> {
                        launchVoiceSearch()
                    }
                    else -> {
                        requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                    }
                }
            } else {
                Toast.makeText(requireContext(), "La búsqueda por voz está desactivada en los ajustes.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun launchVoiceSearch() {
        val speechIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Habla para buscar productos...")
        }
        try {
            speechRecognizerLauncher.launch(speechIntent)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "El reconocimiento de voz no está disponible en este dispositivo.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                viewModel.setSearchQuery(query.orEmpty())
                return true
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.setSearchQuery(newText.orEmpty())
                return true
            }
        })
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
                }
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}