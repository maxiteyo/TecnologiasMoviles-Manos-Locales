package com.example.manoslocales

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.manoslocales.databinding.ActivityLoginBinding
import android.widget.Toast


class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun attachBaseContext(newBase: Context) {
        val lang = LocaleHelper.getSavedLanguage(newBase)
        val context = LocaleHelper.setLocale(newBase, lang)
        super.attachBaseContext(context)
    }


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


            binding.botonLogin.setOnClickListener {
                val email = binding.inputMail.text.toString().trim()
                val password = binding.inputPassword.text.toString()

                if (email.isEmpty()) {
                    binding.inputMail.error = "El campo email es obligatorio"
                    return@setOnClickListener
                }

                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    binding.inputMail.error = "El email no es válido"
                    return@setOnClickListener
                }

                //Contraseña debe contener una mayus, minimo 8 caracteres y un numero
                val passwordRegex = Regex("^(?=.*[A-Z])(?=.*\\d).{8,}$")
                if (!passwordRegex.matches(password)) {
                    binding.inputPassword.error = "La contraseña debe tener mínimo 8 caracteres, una mayúscula y un número"
                    return@setOnClickListener
                }

                if (password.isEmpty()) {
                    binding.inputPassword.error = "El campo contraseña es obligatorio"
                    return@setOnClickListener
                }

                // Usuario hardcodeado a modo de ejmplo --> a futuro se checkea con una base de datos

                if (email == "usuario@gmail.com" && password == "Prueba123") {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show()
                }
            }

        binding.nuevaCuenta.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.olvidasteContra.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }
    }
}