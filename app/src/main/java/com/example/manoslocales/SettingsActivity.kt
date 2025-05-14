package com.example.manoslocales

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CompoundButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.manoslocales.databinding.ActivitySettingsBinding
import java.util.*

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    override fun attachBaseContext(newBase: Context) {
        val sharedPrefs = newBase.getSharedPreferences("app_settings", MODE_PRIVATE)
        val language = sharedPrefs.getString("idioma_codigo", "es") ?: "es"
        val localeUpdatedContext = updateLocale(newBase, language)
        super.attachBaseContext(localeUpdatedContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPrefs = getSharedPreferences("app_settings", MODE_PRIVATE)

        // VOLVER
        binding.btnVolver.setOnClickListener {
            finish()
        }

        // MODO OSCURO
        val isDarkMode = sharedPrefs.getBoolean("dark_mode", false)
        binding.switchDarkMode.isChecked = isDarkMode

        AppCompatDelegate.setDefaultNightMode(
            if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )

        binding.switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            sharedPrefs.edit().putBoolean("dark_mode", isChecked).apply()
            AppCompatDelegate.setDefaultNightMode(
                if (isChecked) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            )
        }

        // NOTIFICACIONES
        val notificacionesActivas = sharedPrefs.getBoolean("notificaciones", true)
        binding.switchNotificaciones.isChecked = notificacionesActivas

        binding.switchNotificaciones.setOnCheckedChangeListener { _: CompoundButton, isChecked: Boolean ->
            sharedPrefs.edit().putBoolean("notificaciones", isChecked).apply()
            val msg = if (isChecked) "Notificaciones activadas" else "Notificaciones desactivadas"
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        }

        // IDIOMA
        val idiomas = listOf("Español", "Inglés", "Portugués")
        val codigos = listOf("es", "en", "pt")

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, idiomas)
        binding.spinnerIdioma.adapter = adapter

        val idiomaGuardado = sharedPrefs.getString("idioma_codigo", "es")
        val indexGuardado = codigos.indexOf(idiomaGuardado)
        if (indexGuardado >= 0) binding.spinnerIdioma.setSelection(indexGuardado)

        binding.spinnerIdioma.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val nuevoCodigo = codigos[position]
                if (nuevoCodigo != idiomaGuardado) {
                    sharedPrefs.edit().putString("idioma_codigo", nuevoCodigo).apply()

                    // Reiniciar la app (puede ser MainActivity o LoginActivity)
                    val intent = Intent(this@SettingsActivity, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

    }

    private fun updateLocale(context: Context, language: String): Context {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        return context.createConfigurationContext(config)
    }
}


