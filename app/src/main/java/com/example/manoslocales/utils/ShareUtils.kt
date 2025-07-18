package com.example.manoslocales.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

/**
 * Descarga una imagen desde una URL, la guarda en caché y devuelve su Uri.
 * Esta es una función de suspensión porque realiza operaciones de red y de disco.
 *
 * @param context El contexto de la aplicación.
 * @param imageUrl La URL de la imagen a descargar.
 * @return La Uri de la imagen guardada, o null si falla.
 */
private suspend fun getLocalImageUri(context: Context, imageUrl: String): Uri? {
    return withContext(Dispatchers.IO) {
        try {
            // Descargar la imagen usando Glide
            val bitmap = Glide.with(context)
                .asBitmap()
                .load(imageUrl)
                .submit()
                .get()

            // Guardar la imagen en el directorio de caché
            val cachePath = File(context.cacheDir, "images")
            cachePath.mkdirs() // Crear el directorio si no existe
            val file = File(cachePath, "shared_image.png")
            val fileOutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
            fileOutputStream.close()

            // Obtener la Uri usando FileProvider
            FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

/**
 * Abre el selector de aplicaciones para compartir texto y una imagen.
 *
 * @param context El contexto de la aplicación.
 * @param productName El nombre del producto.
 * @param productDescription La descripción del producto.
 * @param producerName El nombre del productor.
 * @param productImageUrl La URL de la imagen del producto.
 */
suspend fun shareGeneric(context: Context, productName: String, productDescription: String, producerName: String, productImageUrl: String) {
    val imageUri = getLocalImageUri(context, productImageUrl)
    if (imageUri == null) {
        Toast.makeText(context, "Error al compartir la imagen.", Toast.LENGTH_SHORT).show()
        return
    }

    val deepLink = "https://play.google.com/store/apps/details?id=${context.packageName}"
    val shareText = """
    ¡Mira este producto en Manos Locales!

    *$productName*
    $productDescription
    
    Productor: $producerName

    Descarga la app: $deepLink
    """.trimIndent()

    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "image/png"
        putExtra(Intent.EXTRA_STREAM, imageUri)
        putExtra(Intent.EXTRA_TEXT, shareText)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(shareIntent, "Compartir producto"))
}

/**
 * Intenta compartir texto y una imagen a través de WhatsApp.
 *
 * @param context El contexto de la aplicación.
 * @param productName El nombre del producto.
 * @param productDescription La descripción del producto.
 * @param producerName El nombre del productor.
 * @param productImageUrl La URL de la imagen del producto.
 */
suspend fun shareOnWhatsApp(context: Context, productName: String, productDescription: String, producerName: String, productImageUrl: String) {
    val imageUri = getLocalImageUri(context, productImageUrl)
    if (imageUri == null) {
        Toast.makeText(context, "Error al compartir la imagen.", Toast.LENGTH_SHORT).show()
        return
    }

    val deepLink = "https://play.google.com/store/apps/details?id=${context.packageName}"
    val shareText = """
    ¡Mira este producto en Manos Locales!

    *$productName*
    $productDescription
    
    Productor: $producerName

    Descarga la app: $deepLink
    """.trimIndent()

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "image/png"
        `package` = "com.whatsapp"
        putExtra(Intent.EXTRA_STREAM, imageUri)
        putExtra(Intent.EXTRA_TEXT, shareText)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    try {
        context.startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(context, "WhatsApp no está instalado.", Toast.LENGTH_SHORT).show()
    }
}

/**
 * Abre la aplicación de Instagram.
 *
 * @param context El contexto de la aplicación.
 * @param productImageUrl La URL de la imagen del producto.
 */
suspend fun shareOnInstagram(context: Context, productImageUrl: String) {
    val imageUri = getLocalImageUri(context, productImageUrl)
    if (imageUri == null) {
        Toast.makeText(context, "Error al compartir la imagen.", Toast.LENGTH_SHORT).show()
        return
    }

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "image/png"
        `package` = "com.instagram.android"
        putExtra(Intent.EXTRA_STREAM, imageUri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    try {
        context.startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(context, "Instagram no está instalado.", Toast.LENGTH_SHORT).show()
    }
}