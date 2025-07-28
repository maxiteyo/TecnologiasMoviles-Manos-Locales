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
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.manoslocales.databinding.ActivitySettingsBinding
import java.util.*
import java.util.concurrent.TimeUnit
import android.net.Uri


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
        //probarNotificacion()
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // CONTACTAR AL DESARROLLADOR
        binding.btnContactDeveloper.setOnClickListener {
            sendEmailToDeveloper()
        }

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

        if (notificacionesActivas) {
            programarRecordatorio()
        } else {
            cancelarRecordatorio()
        }

        binding.switchNotificaciones.setOnCheckedChangeListener { _: CompoundButton, isChecked: Boolean ->
            sharedPrefs.edit().putBoolean("notificaciones", isChecked).apply()
            val msg = if (isChecked) getString(R.string.notificacionesactivadas) else getString(R.string.notificacionesdesactivadas)
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
            if (isChecked) {
                programarRecordatorio()
            } else {
                cancelarRecordatorio()
            }
        }

        // BÚSQUEDA POR VOZ
        val isVoiceSearchEnabled = sharedPrefs.getBoolean("voice_search_enabled", true)
        binding.switchVoiceSearch.isChecked = isVoiceSearchEnabled

        binding.switchVoiceSearch.setOnCheckedChangeListener { _, isChecked ->
            sharedPrefs.edit().putBoolean("voice_search_enabled", isChecked).apply()
            val msg = if (isChecked) "Búsqueda por voz activada" else "Búsqueda por voz desactivada"
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        }

        // IDIOMA
        val idiomas = resources.getStringArray(R.array.idiomas_array).toList()
        val codigos = listOf("es", "en", "pt")

        val adapter = ArrayAdapter(this, R.layout.spinner, idiomas).also {
            it.setDropDownViewResource(R.layout.spinner_dropdown)
        }
        binding.spinnerIdioma.adapter = adapter

        val idiomaGuardado = sharedPrefs.getString("idioma_codigo", "es")
        val indexGuardado = codigos.indexOf(idiomaGuardado)
        if (indexGuardado >= 0) binding.spinnerIdioma.setSelection(indexGuardado)

        binding.spinnerIdioma.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val nuevoCodigo = codigos[position]
                if (nuevoCodigo != idiomaGuardado) {
                    sharedPrefs.edit().putString("idioma_codigo", nuevoCodigo).apply()

                    val intent = Intent(this@SettingsActivity, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun programarRecordatorio() {
        val workRequest = PeriodicWorkRequestBuilder<ReminderWorker>(6, TimeUnit.HOURS)
            .setInitialDelay(6, TimeUnit.HOURS)
            .build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "reminder_work",
            androidx.work.ExistingPeriodicWorkPolicy.REPLACE,
            workRequest
        )
    }

    private fun cancelarRecordatorio() {
        WorkManager.getInstance(this).cancelUniqueWork("reminder_work")
    }

    private fun updateLocale(context: Context, language: String): Context {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        return context.createConfigurationContext(config)
    }

    private fun sendEmailToDeveloper() {
        val developerEmail = "ssuppia175@alumnos.iua.edu.ar"
        val subject = "Consulta desde la app Manos Locales"

        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(developerEmail))
            putExtra(Intent.EXTRA_SUBJECT, subject)
        }

        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        } else {
            Toast.makeText(this, "No se encontró una aplicación de correo.", Toast.LENGTH_SHORT).show()
        }
    }

    //Para probar notifiaaciones
    /*private fun probarNotificacion() {
        val workRequest = OneTimeWorkRequestBuilder<ReminderWorker>().build()
        WorkManager.getInstance(this@SettingsActivity).enqueue(workRequest)
    }*/

}