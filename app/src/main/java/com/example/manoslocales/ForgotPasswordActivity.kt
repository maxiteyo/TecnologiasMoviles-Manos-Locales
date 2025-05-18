package com.example.manoslocales

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.manoslocales.ui.theme.ManosLocalesTheme
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        enableEdgeToEdge()

        setContent {
            ManosLocalesTheme {
                ForgotPasswordScreen()
            }
        }
    }
}

@Composable
fun ForgotPasswordScreen() {
    val context = LocalContext.current
    val verdeClaro = colorResource(R.color.verdeclaro)
    val blancoCrema = colorResource(R.color.blancocrema)

    var email by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {
        // Fondo
        Image(
            painter = painterResource(id = R.drawable.fondo),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 40.dp, vertical = 100.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo
            Image(
                painter = painterResource(id = R.drawable.logocompleto),
                contentDescription = "Logo",
                modifier = Modifier
                    .height(200.dp)
                    .width(200.dp)
            )

            Spacer(modifier = Modifier.height(65.dp))

            // Título
            Text(
                text = "Restablecer contraseña",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = verdeClaro
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Campo de email
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = {
                    Text(
                        text = "Correo electrónico",
                        color = verdeClaro,
                        fontWeight = FontWeight.Bold
                    )
                },
                textStyle = TextStyle(color = verdeClaro, fontWeight = FontWeight.Bold),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = verdeClaro,
                    unfocusedBorderColor = verdeClaro,
                    cursorColor = verdeClaro,
                    focusedLabelColor = verdeClaro,
                    unfocusedLabelColor = verdeClaro
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Botón
            Button(
                onClick = {
                    if (email.isBlank()) {
                        message = "Por favor, ingresá tu correo"
                    } else {
                        FirebaseAuth.getInstance()
                            .sendPasswordResetEmail(email)
                            .addOnCompleteListener { task ->
                                message = if (task.isSuccessful) {
                                    "Correo enviado. Revisa tu bandeja de entrada."
                                } else {
                                    "Error: ${task.exception?.localizedMessage ?: "Intentalo de nuevo"}"
                                }
                                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                            }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = verdeClaro,
                    contentColor = blancoCrema
                )
            ) {
                Text(
                    text = "Enviar enlace de recuperación",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Mensaje
            if (message.isNotBlank()) {
                Text(
                    text = message,
                    color = verdeClaro,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
