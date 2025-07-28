package com.example.manoslocales

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import java.util.Calendar
import android.content.Context
import android.widget.Toast
import com.example.manoslocales.databinding.ActivityRegisterBinding
import androidx.activity.OnBackPressedCallback
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlin.text.matches

class RegisterActivity : AppCompatActivity() {

    override fun attachBaseContext(newBase: Context) {
        val lang = LocaleHelper.getSavedLanguage(newBase)
        val context = LocaleHelper.setLocale(newBase, lang)
        super.attachBaseContext(context)
    }

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

    private fun configurarFechaNacimiento() {
        binding.fechaNacimiento.setOnClickListener {
            val calendario = Calendar.getInstance()
            val year = calendario.get(Calendar.YEAR)
            val month = calendario.get(Calendar.MONTH)
            val day = calendario.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(
                this,
                R.style.DatePickerTheme,
                { _, year, month, day ->
                    val selectedDate = String.format("%02d/%02d/%04d", day, month + 1, year)
                    binding.fechaNacimiento.setText(selectedDate)
                },
                year, month, day
            )
            datePicker.show()
        }
    }

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

    private fun configurarBotonRegistrarse() {
        binding.btnRegistrarse.setOnClickListener {
            if (validarCampos()) {
                val email = binding.inputMail.text.toString().trim()
                val password = binding.contra.text.toString().trim()

                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            guardarDatosDeUsuario()
                        } else {
                            Toast.makeText(this, "Error en el registro: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                        }
                    }
            }
        }
    }

    private fun guardarDatosDeUsuario() {
        val firebaseUser = auth.currentUser
        firebaseUser?.let { user ->
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

            db.collection("users").document(user.uid)
                .set(userData)
                .addOnSuccessListener {
                    Toast.makeText(this, getString(R.string.registroexitoso), Toast.LENGTH_LONG).show()
                    auth.signOut()

                    val intent = Intent(this, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error al guardar datos: ${e.message}", Toast.LENGTH_LONG).show()
                }
        }
    }

    private fun validarCampos(): Boolean {
        var esValido = true

        binding.firstName.error = null
        binding.lastName.error = null
        binding.dni.error = null
        binding.telefono.error = null
        binding.inputMail.error = null
        binding.contra.error = null
        binding.confirmarContra.error = null
        binding.nombreEmprendimiento.error = null
        binding.descripcion.error = null


        if (binding.firstName.text.toString().isBlank()) {
            binding.firstName.error = getString(R.string.completarcamposoblicatorios)
            esValido = false
        }

        if (binding.lastName.text.toString().isBlank()) {
            binding.lastName.error = getString(R.string.completarcamposoblicatorios)
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

}