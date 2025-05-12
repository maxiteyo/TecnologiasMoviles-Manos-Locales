package com.example.manoslocales

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import java.util.Calendar
import android.app.DatePickerDialog
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.RadioButton
import android.widget.Toast
import java.util.regex.Pattern
//import androidx.core.view.ViewCompat
//import androidx.core.view.WindowInsetsCompat

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)
        /*ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets*/

        configurarTipoDocumento()
        configurarFechaNacimiento()
        configurarEmprendedor()
        configurarBotonRegistrarse()
        }

    private fun configurarTipoDocumento() {
        val spinner = findViewById<Spinner>(R.id.spTipoDocumento)
        val tipos = arrayOf("DNI", "CUIL", "Pasaporte")
        spinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, tipos)

    }

    private fun configurarFechaNacimiento() {
        val campoFecha = findViewById<EditText>(R.id.fechaNacimiento)
        campoFecha.setOnClickListener {
            val calendario = Calendar.getInstance()
            val anio = calendario.get(Calendar.YEAR)
            val mes = calendario.get(Calendar.MONTH)
            val dia = calendario.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(this, { _, year, month, day ->
                val fecha = String.format("%02d/%02d/%04d", day, month + 1, year)
                campoFecha.setText(fecha)
            }, anio, mes, dia)

            datePicker.datePicker.maxDate = System.currentTimeMillis()
            datePicker.show()
        }
    }

    private fun configurarEmprendedor() {
        val radioGroup = findViewById<RadioGroup>(R.id.rgEmprendedor)
        val camposEmprendedor = listOf(
            R.id.tituloEmprendedor,
            R.id.nombreEmprendimiento,
            R.id.descripcion,
            R.id.direccEmprendimiento,
            R.id.formascontacto
        ).map { findViewById<View>(it) }

        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            val mostrar = checkedId == R.id.rbsi
            camposEmprendedor.forEach { it.visibility = if (mostrar) View.VISIBLE else View.GONE }
        }
    }

    private fun configurarBotonRegistrarse() {
        val boton = findViewById<Button>(R.id.btnRegistrarse)
        boton.setOnClickListener {
            if (validarCampos()) {
                Toast.makeText(this, "Registro exitoso", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun validarCampos(): Boolean {
        val camposObligatorios = listOf(
            R.id.first_name, R.id.last_name, R.id.fechaNacimiento,
            R.id.dni, R.id.telefono, R.id.input_mail,
            R.id.contra, R.id.confirmarContra,
            R.id.direccion, R.id.codpostal
        )

        for (id in camposObligatorios) {
            val campo = findViewById<EditText>(id)
            if (campo.text.toString().isBlank()) {
                campo.error = "Campo obligatorio"
                campo.requestFocus()
                return false
            }
        }

        val contra = findViewById<EditText>(R.id.contra).text.toString()
        val confirmar = findViewById<EditText>(R.id.confirmarContra).text.toString()

        if (contra != confirmar) {
            findViewById<EditText>(R.id.confirmarContra).error = "Las contraseñas no coinciden"
            return false
        }

        if (!esContrasenaValida(contra)) {
            findViewById<EditText>(R.id.contra).error =
                "Mínimo 8 caracteres, una mayúscula y un número"
            return false
        }

        val aceptaTerminos = findViewById<CheckBox>(R.id.aceptaTerminos)
        if (!aceptaTerminos.isChecked) {
            Toast.makeText(this, "Debe aceptar los términos", Toast.LENGTH_SHORT).show()
            return false
        }

        val rbSi = findViewById<RadioButton>(R.id.rbsi)
        if (rbSi.isChecked) {
            val camposEmprendedor = listOf(
                R.id.nombreEmprendimiento,
                R.id.descripcion,
                R.id.direccEmprendimiento,
                R.id.formascontacto
            )
            for (id in camposEmprendedor) {
                val campo = findViewById<EditText>(id)
                if (campo.visibility == View.VISIBLE && campo.text.toString().isBlank()) {
                    campo.error = "Campo obligatorio"
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
        /*val rgEmprendedor = findViewById<RadioGroup>(R.id.rgEmprendedor)
        val layoutEmprendedor = findViewById<EditText>(R.id.nombreEmprendimiento)

        rgEmprendedor.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.rbSi) {
                layoutEmprendedor.visibility = View.VISIBLE
            } else {
                layoutEmprendedor.visibility = View.GONE
            }
        }

        val btnRegistrarse = findViewById<Button>(R.id.btnRegistrarse)
        btnRegistrarse.setOnClickListener {
            val acepto = findViewById<CheckBox>(R.id.aceptaTerminos).isChecked
            if (!acepto) {
                Toast.makeText(this, "Debes aceptar los términos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Aquí podés validar campos y continuar con el registro
        }*/



    }
}