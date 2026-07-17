package com.example.compressorapp

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import java.io.File

/**
 * Copie le contenu d'un Uri (choisi depuis la galerie) vers un fichier
 * temporaire dans le cache de l'application. Compressor a besoin d'un
 * objet File en entrée, pas directement d'un Uri.
 */
fun copyUriToFile(context: Context, uri: Uri, fileName: String = "original_${System.currentTimeMillis()}.jpg"): File {
    val cacheDir = File(context.cacheDir, "images").apply { mkdirs() }
    val outputFile = File(cacheDir, fileName)
    context.contentResolver.openInputStream(uri)?.use { input ->
        outputFile.outputStream().use { output ->
            input.copyTo(output)
        }
    }
    return outputFile
}

/**
 * Enregistre un fichier image dans la galerie publique (dossier Pictures/CompressorApp)
 * en utilisant MediaStore, compatible avec le stockage limité (scoped storage).
 */
fun saveFileToGallery(context: Context, file: File): Boolean {
    return try {
        val resolver = context.contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, file.name)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/CompressorApp")
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }
        }

        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            ?: return false

        resolver.openOutputStream(uri)?.use { out ->
            file.inputStream().use { input -> input.copyTo(out) }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            contentValues.clear()
            contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
            resolver.update(uri, contentValues, null, null)
        }

        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}

/** Formate une taille en octets vers un texte lisible (Ko / Mo). */
fun formatFileSize(bytes: Long): String {
    val kb = bytes / 1024.0
    return if (kb < 1024) {
        String.format("%.1f Ko", kb)
    } else {
        String.format("%.2f Mo", kb / 1024.0)
    }
}
