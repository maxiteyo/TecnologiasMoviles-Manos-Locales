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

class HomeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ProductAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewProducts)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val productList = listOf(
            Product(1, "Miel Orgánica", "Miel natural cosechada a mano.", "Alimentos",1200.0, "María Gómez", R.drawable.mielorganica    ),
            Product(2, "Queso de cabra", "Queso fresco elaborado artesanalmente.", "Alimentos",850.0, "Pedro Sánchez", R.drawable.quesocabra),
            Product(3, "Frutillas frescas", "Frutillas sin pesticidas, recién cosechadas.", "Alimentos",500.0, "Juan Pérez",R.drawable.frutillasfrescas),
            Product(4, "Nueces", "Nueces naturales", "Alimentos",250.0,"Maximo Teyo", R.drawable.nueces),
            Product(5, "Sueter de lana", "Sueter de lana hecho a mano", "Textiles" ,2000.0, "Sofia Suppia", R.drawable.sueter ),
            Product(6,"Jarrón artesanal","Jarrón artesanal pintado a mano" ,"Artesanías",5000.0, "Lionel Messi", R.drawable.jarron),
            Product(7,"Aceite de almendra","Aceite de almendra hidratante","Cosmética natural",700.0 , "Pepe Salamandra", R.drawable.aceite),
            Product(8, "Autito de madera", "Autito de madera artesanal", "Artesanías", 1300.0, "Guido Kaczka", R.drawable.autito)
        )

        adapter = ProductAdapter(productList) { selectedProduct ->
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

        return view
    }
}