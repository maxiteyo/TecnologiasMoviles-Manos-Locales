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
import android.util.Log
import android.widget.Toast
import com.example.manoslocales.databinding.ActivityRegisterBinding
import java.util.regex.Pattern
import androidx.activity.OnBackPressedCallback
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlin.or
import kotlin.text.matches
import kotlin.text.set
import kotlin.toString


class RegisterActivity : AppCompatActivity() {

    override fun attachBaseContext(newBase: Context) {
        val lang = LocaleHelper.getSavedLanguage(newBase)
        val context = LocaleHelper.setLocale(newBase, lang)
        super.attachBaseContext(context)
    }


    private lateinit var binding: ActivityRegisterBinding
    // Declara las variables de Firebase
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializa Firebase Auth y Firestore
        auth = Firebase.auth
        db = FirebaseFirestore.getInstance()

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
        val adapter = ArrayAdapter(
            this,
            R.layout.spinner,
            tipos
        ).also {
            it.setDropDownViewResource(R.layout.spinner_dropdown)
        }
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
                val email = binding.inputMail.text.toString().trim()
                val password = binding.contra.text.toString().trim()

                // 1. Crear el usuario en Firebase Authentication
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Usuario creado con éxito, ahora guardamos sus datos
                            guardarDatosDeUsuario()
                        } else {
                            // Si el registro falla, muestra un mensaje al usuario.
                            Toast.makeText(this, "Error en el registro: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                        }
                    }
            }
            // Se elimina el "else" que mostraba el Toast genérico.
            // Ahora los errores se verán en cada campo individualmente.
        }
    }

    private fun guardarDatosDeUsuario() {
        val firebaseUser = auth.currentUser
        firebaseUser?.let { user ->
            // Recopilar todos los datos del formulario en un mapa
            val userData = hashMapOf(
                "uid" to user.uid,
                "nombre" to binding.firstName.text.toString(),
                "apellido" to binding.lastName.text.toString(),
                "fechaNacimiento" to binding.fechaNacimiento.text.toString(),
                "tipoDocumento" to binding.spTipoDocumento.selectedItem.toString(),
                "dni" to binding.dni.text.toString(),
                "telefono" to binding.telefono.text.toString(),
                "email" to user.email,
                "direccion" to binding.direccion.text.toString(),
                "codigoPostal" to binding.codpostal.text.toString(),
                "esEmprendedor" to binding.rbsi.isChecked
            )

            if (binding.rbsi.isChecked) {
                userData["nombreEmprendimiento"] = binding.nombreEmprendimiento.text.toString()
                userData["descripcionEmprendimiento"] = binding.descripcion.text.toString()
                userData["direccionEmprendimiento"] = binding.direccEmprendimiento.text.toString()
                userData["contactoEmprendimiento"] = binding.formascontacto.text.toString()
            }

            // Guardar el mapa en un documento de Firestore
            db.collection("users").document(user.uid)
                .set(userData)
                .addOnSuccessListener {
                    // 1. Datos guardados con éxito, mostrar mensaje.
                    Toast.makeText(this, getString(R.string.registroexitoso), Toast.LENGTH_LONG).show()

                    // 2. Redirigir a la pantalla de Login.
                    val intent = Intent(this, LoginActivity::class.java)
                    // Estas flags limpian la pila de actividades para que el usuario no pueda volver a la pantalla de registro.
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish() // Cierra RegisterActivity
                }
                .addOnFailureListener { e ->
                    // Error al guardar los datos
                    Toast.makeText(this, "Error al guardar datos: ${e.message}", Toast.LENGTH_LONG).show()
                }
        }
    }


    // --- Validación general de campos
    private fun validarCampos(): Boolean {
        var esValido = true

        // Limpiar errores previos
        binding.firstName.error = null
        binding.lastName.error = null
        binding.dni.error = null
        binding.telefono.error = null
        binding.inputMail.error = null
        binding.contra.error = null
        binding.confirmarContra.error = null
        binding.nombreEmprendimiento.error = null
        binding.descripcion.error = null

        // --- Validaciones individuales ---

        if (binding.firstName.text.toString().isBlank()) {
            binding.firstName.error = getString(R.string.completarcamposoblicatorios) // Mensaje más específico
            esValido = false
        }

        if (binding.lastName.text.toString().isBlank()) {
            binding.lastName.error = getString(R.string.completarcamposoblicatorios) // Mensaje más específico
            esValido = false
        }

        val dni = binding.dni.text.toString()
        if (dni.length < 7 || dni.length > 8) {
            binding.dni.error = getString(R.string.cantdigitosdni)
            esValido = false
        }

        val telefono = binding.telefono.text.toString()
        if (!telefono.matches(Regex("^\\d{8,10}$"))) {
            binding.telefono.error = getString(R.string.cantdigitostelefono)
            esValido = false
        }

        val email = binding.inputMail.text.toString()
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.inputMail.error = getString(R.string.correoinvalido)
            esValido = false
        }

        val contra = binding.contra.text.toString()
        val confirmar = binding.confirmarContra.text.toString()
        val regex = Regex("^(?=.*[A-Z])(?=.*\\d).{8,}$")
        if (!regex.matches(contra)) {
            binding.contra.error = getString(R.string.minimocontra)
            esValido = false
        }

        if (contra != confirmar) {
            binding.confirmarContra.error = getString(R.string.nocoincidecontra)
            esValido = false
        }

        if (!binding.aceptaTerminos.isChecked) {
            Toast.makeText(this, getString(R.string.debeaceptarterminos), Toast.LENGTH_SHORT).show()
            esValido = false
        }

        // Validar campos de emprendimiento si es necesario
        if (binding.rbsi.isChecked) {
            if (binding.nombreEmprendimiento.text.toString().isBlank()) {
                binding.nombreEmprendimiento.error = getString(R.string.completarcamposoblicatorios)
                esValido = false
            }
            if (binding.descripcion.text.toString().isBlank()) {
                binding.descripcion.error = getString(R.string.completarcamposoblicatorios)
                esValido = false
            }
        }

        return esValido
    }


    private fun esContrasenaValida(contra: String): Boolean {
        val patron = Pattern.compile("^(?=.*[A-Z])(?=.*\\d).{8,}$")
        return patron.matcher(contra).matches()
    }


}
