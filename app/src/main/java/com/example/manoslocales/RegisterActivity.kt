package com.example.manoslocales

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.manoslocales.databinding.ActivityRegisterBinding
import java.util.*
import java.util.regex.Pattern


class RegisterActivity : AppCompatActivity() {

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

    }

    // --- Spinner: Tipo de documento
    private fun configurarTipoDocumento() {
        val tipos = arrayOf("DNI", "CUIL", "Pasaporte")

        val adapter = object : ArrayAdapter<String>(
            this,
            android.R.layout.simple_spinner_item,
            tipos
        ) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent)
                (view as TextView).setTextColor(getColor(R.color.verdeclaro)) // color texto seleccionado
                view.textAlignment = View.TEXT_ALIGNMENT_CENTER
                return view
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val dropView = super.getDropDownView(position, convertView, parent)
                (dropView as TextView).setTextColor(getColor(R.color.verdeoscuro)) // color en el desplegable
                dropView.setBackgroundColor(getColor(R.color.blancocrema))
                dropView.textAlignment = View.TEXT_ALIGNMENT_CENTER
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
                Toast.makeText(this, "Registro exitoso", Toast.LENGTH_LONG).show()

                // Redirige a LoginActivity después del registro
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish() // Evita que pueda volver al registro con el botón atrás
            }
        }
    }

    // --- Validación general de campos
    private fun validarCampos(): Boolean {
        var valido = true

        val dni = binding.dni.text.toString()
        if (dni.length != 8) {
            binding.dni.error = "El DNI debe tener exactamente 8 dígitos"
            valido = false
        }

        val telefono = binding.telefono.text.toString()
        if (!telefono.matches(Regex("^\\d{8,15}$"))) {
            binding.telefono.error = "Teléfono inválido. Solo números, entre 8 y 15 dígitos"
            valido = false
        }

        val email = binding.inputMail.text.toString()
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.inputMail.error = "Correo inválido"
            valido = false
        }

        val contra = binding.contra.text.toString()
        val confirmar = binding.confirmarContra.text.toString()
        val regex = Regex("^(?=.*[A-Z])(?=.*\\d).{8,}$")
        if (!regex.matches(contra)) {
            binding.contra.error = "Debe tener mínimo 8 caracteres, una mayúscula y un número"
            valido = false
        } else if (contra != confirmar) {
            binding.confirmarContra.error = "Las contraseñas no coinciden"
            valido = false
        }

        if (!binding.aceptaTerminos.isChecked) {
            Toast.makeText(this, "Debe aceptar los términos y condiciones", Toast.LENGTH_SHORT).show()
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
                    campo.requestFocus()
                    return false
                }
            }
        }

        return true
    }

    private fun esContrasenaValida(contra: String): Boolean {
        val patron = Pattern.compile("^(?=.*[A-Z])(?=.*\\d).{8,}$")
        return patron.matcher(contra).matches()
    }


}
