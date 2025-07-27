package com.example.manoslocales

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.manoslocales.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import java.util.*

class ProfileFragment : Fragment() {

    private lateinit var editTextNombre: EditText
    private lateinit var editTextApellido: EditText
    private lateinit var editTextFechaNacimiento: EditText
    private lateinit var editTextDNI: EditText
    private lateinit var editTextTelefono: EditText
    private lateinit var editTextEmail: EditText
    private lateinit var btnGuardar: Button
    private lateinit var btnCerrarSesion: Button
    private lateinit var btnSettings: Button

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        editTextNombre = view.findViewById(R.id.editTextNombre)
        editTextApellido = view.findViewById(R.id.editTextApellido)
        editTextFechaNacimiento = view.findViewById(R.id.editTextFechaNacimiento)
        editTextDNI = view.findViewById(R.id.editTextDNI)
        editTextTelefono = view.findViewById(R.id.editTextTelefono)
        editTextEmail = view.findViewById(R.id.editTextEmail)
        btnGuardar = view.findViewById(R.id.btnGuardar)
        btnCerrarSesion = view.findViewById(R.id.btnCerrarSesion)
        btnSettings = view.findViewById(R.id.btnSettings)

        cargarDatosDeUsuario()

        editTextFechaNacimiento.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(requireContext(), R.style.DatePickerTheme, { _, y, m, d ->
                val fecha = String.format("%02d/%02d/%04d", d, m + 1, y)
                editTextFechaNacimiento.setText(fecha)
            }, year, month, day)

            datePicker.show()
        }

        btnGuardar.setOnClickListener {
            guardarCambiosEnFirestore()
        }

        btnCerrarSesion.setOnClickListener {
            auth.signOut()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        btnSettings.setOnClickListener {
            val intent = Intent(requireContext(), SettingsActivity::class.java)
            startActivity(intent)
        }
    }

    private fun cargarDatosDeUsuario() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(context, "Error: Usuario no autenticado.", Toast.LENGTH_SHORT).show()
            return
        }
        val userId = currentUser.uid

        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val user = document.toObject(User::class.java)
                    user?.let {
                        editTextNombre.setText(it.nombre)
                        editTextApellido.setText(it.apellido)
                        editTextFechaNacimiento.setText(it.fechaNacimiento)
                        editTextDNI.setText(it.numeroDocumento)
                        editTextTelefono.setText(it.telefono)
                        editTextEmail.setText(it.email)
                    }
                } else {
                    Toast.makeText(context, "Completa tu perfil por primera vez.", Toast.LENGTH_SHORT).show()
                    editTextEmail.setText(currentUser.email)
                }
                editTextEmail.isEnabled = false
            }
            .addOnFailureListener { exception ->
                Toast.makeText(context, "Error al cargar datos: ${exception.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun guardarCambiosEnFirestore() {
        val userId = auth.currentUser?.uid ?: return

        val nombre = editTextNombre.text.toString().trim()
        val apellido = editTextApellido.text.toString().trim()
        val fechaNacimiento = editTextFechaNacimiento.text.toString().trim()
        val numeroDocumento = editTextDNI.text.toString().trim()
        val telefono = editTextTelefono.text.toString().trim()
        val email = editTextEmail.text.toString().trim()

        if (nombre.isEmpty() || apellido.isEmpty() || fechaNacimiento.isEmpty() || numeroDocumento.isEmpty() || telefono.isEmpty()) {
            Toast.makeText(requireContext(), getString(R.string.completarcamposoblicatorios), Toast.LENGTH_SHORT).show()
            return
        }
        if (numeroDocumento.length != 8 || !numeroDocumento.all { it.isDigit() }) {
            Toast.makeText(requireContext(), getString(R.string.cantdigitosdni), Toast.LENGTH_SHORT).show()
            return
        }
        if (telefono.length > 10 || !telefono.all { it.isDigit() }) {
            Toast.makeText(requireContext(), getString(R.string.cantdigitostelefono), Toast.LENGTH_SHORT).show()
            return
        }

        val datosUsuario = mapOf(
            "nombre" to nombre,
            "apellido" to apellido,
            "fechaNacimiento" to fechaNacimiento,
            "numeroDocumento" to numeroDocumento,
            "telefono" to telefono,
            "email" to email
        )

        db.collection("users").document(userId)
            .set(datosUsuario, SetOptions.merge())
            .addOnSuccessListener {
                Toast.makeText(context, getString(R.string.datosguardados), Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Error al guardar: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
    }
}