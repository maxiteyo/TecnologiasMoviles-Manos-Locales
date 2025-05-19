package com.example.manoslocales

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.manoslocales.R
import com.example.manoslocales.adapters.ProductAdapter
import com.example.manoslocales.models.Product
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.AdapterView
import android.widget.EditText
import android.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.activity.OnBackPressedCallback
import androidx.compose.ui.res.stringResource




class HomeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ProductAdapter

    // para el filtrado. Conservo mi lista original y solo actualizo la filtrada
    private lateinit var originalProductList: List<Product>
    private var filteredProductList: List<Product> = listOf()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewProducts)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val context = requireContext()
        originalProductList = listOf(
            Product(1, "Miel Orgánica", "Miel natural cosechada a mano.", context.getString(R.string.alimentos),1200.0, "María Gómez", R.drawable.mielorganica),
            Product(2, "Queso de cabra", "Queso fresco elaborado artesanalmente.", context.getString(R.string.alimentos),850.0, "Pedro Sánchez", R.drawable.quesocabra),
            Product(3, "Frutillas frescas", "Frutillas sin pesticidas, recién cosechadas.", context.getString(R.string.alimentos),500.0, "Juan Pérez",R.drawable.frutillasfrescas),
            Product(4, "Nueces", "Nueces naturales", context.getString(R.string.alimentos),250.0,"Maximo Teyo", R.drawable.nueces),
            Product(5, "Sueter de lana", "Sueter de lana hecho a mano", context.getString(R.string.textiles) ,2000.0, "Sofia Suppia", R.drawable.sueter),
            Product(6,"Jarrón artesanal","Jarrón artesanal pintado a mano" ,context.getString(R.string.artesanias),5000.0, "Lionel Messi", R.drawable.jarron),
            Product(7,"Aceite de almendra","Aceite de almendra hidratante",context.getString(R.string.cosmeticanatural),700.0 , "Pepe Salamandra", R.drawable.aceite),
            Product(8, "Autito de madera", "Autito de madera artesanal", context.getString(R.string.artesanias), 1300.0, "Guido Kaczka", R.drawable.autito)
        )

        filteredProductList = originalProductList


        val spinner = view.findViewById<Spinner>(R.id.spinnerCategory)
        val categories = resources.getStringArray(R.array.categorias_array).toList()

        val spinnerAdapter = ArrayAdapter(requireContext(), R.layout.spinner_item, categories)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = spinnerAdapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                filterProducts()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // No hacer nada
            }
        }

        val searchView = view.findViewById<SearchView>(R.id.searchView)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                filterProducts()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterProducts()
                return true
            }
        })

        return view
    }

    private fun filterProducts() {
        val selectedCategory = view?.findViewById<Spinner>(R.id.spinnerCategory)?.selectedItem.toString()
        val searchText = view?.findViewById<SearchView>(R.id.searchView)?.query.toString().lowercase()

        filteredProductList = originalProductList.filter { product ->
            val matchesCategory = selectedCategory == getString(R.string.todas) || product.category.equals(selectedCategory, ignoreCase = true)
            val matchesSearch = product.name.lowercase().contains(searchText)
            matchesCategory && matchesSearch
        }

        adapter = ProductAdapter(filteredProductList) { selectedProduct ->
            val action = HomeFragmentDirections.actionHomeFragmentToProductDetailFragment(
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
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    // No hace nada (bloquea "volver")
                }
            }
        )
    }

}