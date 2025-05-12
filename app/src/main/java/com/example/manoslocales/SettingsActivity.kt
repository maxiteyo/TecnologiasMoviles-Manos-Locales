package com.example.manoslocales

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.manoslocales.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Opcional: manejar botón atrás si querés
        binding.btnVolver.setOnClickListener {
            finish()
        }
    }
}
