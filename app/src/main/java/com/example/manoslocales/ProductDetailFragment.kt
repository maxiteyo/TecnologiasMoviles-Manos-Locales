package com.example.manoslocales

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs

class ProductDetailFragment : Fragment() {

    private val args by navArgs<ProductDetailFragmentArgs>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_product_detail, container, false)

        view.findViewById<TextView>(R.id.textViewDetailName).text = args.productName
        view.findViewById<TextView>(R.id.textViewDetailPrice).text = "$${args.productPrice}"
        view.findViewById<TextView>(R.id.textViewDetailDescription).text = args.productDescription
        view.findViewById<TextView>(R.id.textViewDetailCategory).text = getString(R.string.categoria_spinner,args.productCategory)
        view.findViewById<TextView>(R.id.textViewDetailProducer).text = getString(R.string.productor_spinner,args.producerName)
        view.findViewById<ImageView>(R.id.ImageProduct).setImageResource(args.imageResId)

        return view
    }
}