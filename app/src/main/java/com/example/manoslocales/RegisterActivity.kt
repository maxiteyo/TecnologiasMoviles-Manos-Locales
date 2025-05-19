package com.example.manoslocales

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import java.util.Calendar
import android.content.Context
import android.graphics.Typeface
import android.widget.Toast
import com.example.manoslocales.databinding.ActivityRegisterBinding
import java.util.regex.Pattern
import androidx.activity.OnBackPressedCallback


class RegisterActivity : AppCompatActivity() {

    override fun attachBaseContext(newBase: Context) {
        val lang = LocaleHelper.getSavedLanguage(newBase)
        val context = LocaleHelper.setLocale(newBase, lang)
        super.attachBaseContext(context)
    }


    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configurarTipoDocumento()
        configurarFechaNacimiento()
        configurarEmprendedor()
        configurarBotonRegistrarse()

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        })
    }

    // --- Spinner: Tipo de documento
    private fun configurarTipoDocumento() {
        val tipos = resources.getStringArray(R.array.tiposdocumentos)

        val adapter = object : ArrayAdapter<String>(
            this,
            android.R.layout.simple_spinner_item,
            tipos
        ) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent)
                (view as TextView).setTextColor(getColor(R.color.verdeclaro))
                view.textAlignment = View.TEXT_ALIGNMENT_CENTER
                view.setTypeface(null, Typeface.BOLD)
                return view
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val dropView = super.getDropDownView(position, convertView, parent)
                (dropView as TextView).setTextColor(getColor(R.color.verdeclaro)) // color en el desplegable
                dropView.setBackgroundColor(getColor(R.color.blancocrema))
                dropView.textAlignment = View.TEXT_ALIGNMENT_CENTER
                dropView.setTypeface(null, Typeface.BOLD)
                return dropView
            }
        }

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spTipoDocumento.adapter = adapter
    }



    // --- DatePicker: Fecha de nacimiento
    private fun configurarFechaNacimiento() {
        binding.fechaNacimiento.setOnClickListener {
            val calendario = Calendar.getInstance()
            val year = calendario.get(Calendar.YEAR)
            val month = calendario.get(Calendar.MONTH)
            val day = calendario.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(
                this,
                R.style.DatePickerTheme, // Aplicamos estilo personalizado
                { _, year, month, day ->
                    val selectedDate = String.format("%02d/%02d/%04d", day, month + 1, year)
                    binding.fechaNacimiento.setText(selectedDate)
                },
                year, month, day
            )
            datePicker.show()
        }
    }

    // --- Mostrar/Ocultar campos según si es emprendedor
    private fun configurarEmprendedor() {
        val campos = listOf(
            binding.tituloEmprendedor,
            binding.nombreEmprendimiento,
            binding.descripcion,
            binding.direccEmprendimiento,
            binding.formascontacto
        )

        binding.rgEmprendedor.setOnCheckedChangeListener { _, checkedId ->
            val mostrar = checkedId == binding.rbsi.id
            campos.forEach { it.visibility = if (mostrar) View.VISIBLE else View.GONE }
        }
    }

    // --- Botón Registrarse
    private fun configurarBotonRegistrarse() {
        binding.btnRegistrarse.setOnClickListener {
            if (validarCampos()) {
                Toast.makeText(this, getString(R.string.registroexitoso), Toast.LENGTH_LONG).show()

                // Redirige a MainActivity después del registro
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            } else {
                Toast.makeText(this, getString(R.string.completarcamposoblicatorios), Toast.LENGTH_SHORT).show()
            }
        }
    }


    // --- Validación general de campos
    private fun validarCampos(): Boolean {
        var valido = true

        val dni = binding.dni.text.toString()
        if (dni.length != 8) {
            binding.dni.error = getString(R.string.cantdigitosdni)
            valido = false
        }

        val telefono = binding.telefono.text.toString()
        if (!telefono.matches(Regex("^\\d{8,10}$"))) {
            binding.telefono.error = getString(R.string.cantdigitostelefono)
            valido = false
        }

        val email = binding.inputMail.text.toString()
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.inputMail.error = getString(R.string.correoinvalido)
            valido = false
        }

        val contra = binding.contra.text.toString()
        val confirmar = binding.confirmarContra.text.toString()
        val regex = Regex("^(?=.*[A-Z])(?=.*\\d).{8,}$")
        if (!regex.matches(contra)) {
            binding.contra.error = getString(R.string.minimocontra)
            valido = false
        }

        if (contra != confirmar) {
            binding.confirmarContra.error = getString(R.string.nocoincidecontra)
            valido = false
        }

        if (!binding.aceptaTerminos.isChecked) {
            Toast.makeText(this, getString(R.string.debeaceptarterminos), Toast.LENGTH_SHORT).show()
            valido = false
        }

        // Validar campos de emprendimiento si es necesario
        if (binding.rbsi.isChecked) {
            val camposEmprendedor = listOf(
                binding.nombreEmprendimiento to "Nombre del emprendimiento",
                binding.descripcion to "Descripción",
                binding.direccEmprendimiento to "Dirección",
                binding.formascontacto to "Formas de contacto"
            )

            for ((campo, nombre) in camposEmprendedor) {
                if (campo.visibility == View.VISIBLE && campo.text.toString().isBlank()) {
                    campo.error = "$nombre es obligatorio"
                    valido = false
                }
            }
        }

        return valido
    }


    private fun esContrasenaValida(contra: String): Boolean {
        val patron = Pattern.compile("^(?=.*[A-Z])(?=.*\\d).{8,}$")
        return patron.matcher(contra).matches()
    }


}
