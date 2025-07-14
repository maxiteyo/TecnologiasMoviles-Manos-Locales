package com.example.manoslocales

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Asignar vistas
        editTextNombre = view.findViewById(R.id.editTextNombre)
        editTextApellido = view.findViewById(R.id.editTextApellido)
        editTextFechaNacimiento = view.findViewById(R.id.editTextFechaNacimiento)
        editTextDNI = view.findViewById(R.id.editTextDNI)
        editTextTelefono = view.findViewById(R.id.editTextTelefono)
        editTextEmail = view.findViewById(R.id.editTextEmail)
        btnGuardar = view.findViewById(R.id.btnGuardar)
        btnCerrarSesion = view.findViewById(R.id.btnCerrarSesion)
        btnSettings = view.findViewById(R.id.btnSettings)

        val prefs = requireContext().getSharedPreferences("user_data", Context.MODE_PRIVATE)

        // Cargar datos guardados
        editTextNombre.setText(prefs.getString("nombre", "usuario"))
        editTextApellido.setText(prefs.getString("apellido", "usuario"))
        editTextFechaNacimiento.setText(prefs.getString("fechaNacimiento", "31/10/2000"))
        editTextDNI.setText(prefs.getString("dni", "12345678"))
        editTextTelefono.setText(prefs.getString("telefono", "3517892308"))
        editTextEmail.setText(prefs.getString("email", "usuario@gmail.com"))

        // DatePicker para fecha de nacimiento
        editTextFechaNacimiento.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(requireContext(),R.style.DatePickerTheme, { _, y, m, d ->
                val fecha = String.format("%02d/%02d/%04d", d, m + 1, y)
                editTextFechaNacimiento.setText(fecha)
            }, year, month, day)

            datePicker.show()
        }

        // Guardar cambios
        btnGuardar.setOnClickListener {
            val nombre = editTextNombre.text.toString().trim()
            val apellido = editTextApellido.text.toString().trim()
            val fechaNacimiento = editTextFechaNacimiento.text.toString().trim()
            val dni = editTextDNI.text.toString().trim()
            val telefono = editTextTelefono.text.toString().trim()
            val email = editTextEmail.text.toString().trim()

            // Validaciones
            when {
                nombre.isEmpty() || apellido.isEmpty() || fechaNacimiento.isEmpty() || dni.isEmpty() || telefono.isEmpty() || email.isEmpty() -> {
                    Toast.makeText(requireContext(), getString(R.string.completarcamposoblicatorios), Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                dni.length != 8 || !dni.all { it.isDigit() } -> {
                    Toast.makeText(requireContext(), getString(R.string.cantdigitosdni), Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                telefono.length > 10 || !telefono.all { it.isDigit() } -> {
                    Toast.makeText(requireContext(), getString(R.string.cantdigitostelefono), Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                    Toast.makeText(requireContext(), getString(R.string.correoinvalido), Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            }

            with(prefs.edit()) {
                putString("nombre", nombre)
                putString("apellido", apellido)
                putString("fechaNacimiento", fechaNacimiento)
                putString("dni", dni)
                putString("telefono", telefono)
                putString("email", email)
                apply()
            }

            Toast.makeText(requireContext(), getString(R.string.datosguardados), Toast.LENGTH_SHORT).show()
        }


        // Acción cerrar sesión
        btnCerrarSesion.setOnClickListener {
            // Limpia los datos (opcional)
            prefs.edit().clear().apply()

            // Ir a LoginActivity
            val intent = android.content.Intent(requireContext(), LoginActivity::class.java)
            intent.flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        // Ir a settings
        btnSettings.setOnClickListener {
            val intent = android.content.Intent(requireContext(), SettingsActivity::class.java)
            startActivity(intent)
        }
    }
}
