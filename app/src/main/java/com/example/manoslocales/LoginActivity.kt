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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import kotlin.or
import kotlin.toString


class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    // Declara la variable de Firebase Auth
    private lateinit var auth: FirebaseAuth

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

        // Inicializa Firebase Auth
        auth = Firebase.auth

        binding.botonLogin.setOnClickListener {
            val email = binding.inputMail.text.toString().trim()
            val password = binding.inputPassword.text.toString()

            if (email.isEmpty()) {
                binding.inputMail.error = getString(R.string.campoemailobligatorio)
                return@setOnClickListener
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.inputMail.error = getString(R.string.correoinvalido)
                return@setOnClickListener
            }

            val passwordRegex = Regex("^(?=.*[A-Z])(?=.*\\d).{8,}$")
            if (!passwordRegex.matches(password)) {
                binding.inputPassword.error = getString(R.string.minimocontra)
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                binding.inputPassword.error = getString(R.string.contraobligatorio)
                return@setOnClickListener
            }

            // Iniciar sesión con Firebase
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Login exitoso
                        Toast.makeText(this, "Login exitoso.", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    } else {
                        // Si el login falla, maneja los errores específicos.
                        try {
                            throw task.exception!!
                        } catch (e: FirebaseAuthInvalidUserException) {
                            // Error: El correo no está registrado.
                            Toast.makeText(this, getString(R.string.usuarioocontramal), Toast.LENGTH_LONG).show()
                        } catch (e: FirebaseAuthInvalidCredentialsException) {
                            // Error: La contraseña es incorrecta.
                            Toast.makeText(this, getString(R.string.usuarioocontramal), Toast.LENGTH_LONG).show()
                        } catch (e: Exception) {
                            // Otro tipo de error.
                            Toast.makeText(this, "Error: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                        }
                    }
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