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
        editTextNombre.setText(prefs.getString("nombre", ""))
        editTextApellido.setText(prefs.getString("apellido", ""))
        editTextFechaNacimiento.setText(prefs.getString("fechaNacimiento", ""))
        editTextDNI.setText(prefs.getString("dni", ""))
        editTextTelefono.setText(prefs.getString("telefono", ""))
        editTextEmail.setText(prefs.getString("email", ""))

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
            with(prefs.edit()) {
                putString("nombre", editTextNombre.text.toString())
                putString("apellido", editTextApellido.text.toString())
                putString("fechaNacimiento", editTextFechaNacimiento.text.toString())
                putString("dni", editTextDNI.text.toString())
                putString("telefono", editTextTelefono.text.toString())
                putString("email", editTextEmail.text.toString())
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
