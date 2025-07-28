package com.example.manoslocales

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.manoslocales.databinding.ActivityLoginBinding
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
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

        auth = Firebase.auth

        val sharedPref = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val savedEmail = sharedPref.getString("SAVED_EMAIL", null)
        if (savedEmail != null) {
            binding.inputMail.setText(savedEmail)
            binding.checkbox.isChecked = true
        }

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
                        val sharedPref = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                        with(sharedPref.edit()) {
                            if (binding.checkbox.isChecked) {
                                putString("SAVED_EMAIL", email)
                            } else {
                                remove("SAVED_EMAIL")
                            }
                            apply()
                        }
                        Toast.makeText(this, "Login exitoso.", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    } else {
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

    override fun onStart() {
        super.onStart()
        // Comprueba si el usuario ya ha iniciado sesión
        if (auth.currentUser != null) {
            // Si hay un usuario, ve directamente a la pantalla principal
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish() // Cierra LoginActivity para que el usuario no pueda volver
        }
    }
}