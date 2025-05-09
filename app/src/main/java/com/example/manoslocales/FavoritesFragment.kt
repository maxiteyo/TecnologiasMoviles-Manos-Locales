package com.example.manoslocales

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class FavoritesFragment : Fragment() {

    // Este método crea la vista del Fragment
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflamos (cargamos) el layout XML
        return inflater.inflate(R.layout.fragment_favorites, container, false)
    }

}