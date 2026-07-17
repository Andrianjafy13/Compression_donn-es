package com.example.compressorapp

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.format
import id.zelory.compressor.constraint.quality
import id.zelory.compressor.constraint.resolution
import id.zelory.compressor.constraint.size
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    CompressorScreen()
                }
            }
        }
    }
}

@Composable
fun CompressorScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Fichier original copié depuis l'Uri choisi
    var originalFile by remember { mutableStateOf<File?>(null) }
    var originalUri by remember { mutableStateOf<Uri?>(null) }

    // Fichier résultant de la compression
    var compressedFile by remember { mutableStateOf<File?>(null) }

    var isCompressing by remember { mutableStateOf(false) }
    var isSaving by remember { mutableStateOf(false) }

    // Sélecteur d'image (galerie)
    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            originalUri = uri
            compressedFile = null
            originalFile = copyUriToFile(context, uri)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Compression d'image",
            style = MaterialTheme.typography.headlineSmall
        )

        // Bouton pour choisir une image
        Button(
            onClick = { pickImageLauncher.launch("image/*") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Choisir une image")
        }

        // Image originale
        originalFile?.let { file ->
            Text("Image originale", style = MaterialTheme.typography.titleMedium)
            Image(
                painter = rememberAsyncImagePainter(file),
                contentDescription = "Image originale",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
            Text("Taille : ${formatFileSize(file.length())}")

            // Bouton pour compresser
            Button(
                onClick = {
                    isCompressing = true
                    scope.launch {
                        val result = compressImage(context, file)
                        compressedFile = result
                        isCompressing = false
                    }
                },
                enabled = !isCompressing,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isCompressing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Compresser l'image")
                }
            }
        }

        // Résultat de la compression
        compressedFile?.let { file ->
            Divider()
            Text("Résultat compressé", style = MaterialTheme.typography.titleMedium)
            Image(
                painter = rememberAsyncImagePainter(file),
                contentDescription = "Image compressée",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )

            val originalSize = originalFile?.length() ?: 0L
            val compressedSize = file.length()
            val gain = if (originalSize > 0) {
                100 - (compressedSize * 100 / originalSize)
            } else 0

            Text("Taille avant : ${formatFileSize(originalSize)}")
            Text("Taille après : ${formatFileSize(compressedSize)}")
            Text("Réduction : $gain %", style = MaterialTheme.typography.bodyLarge)

            // Bouton pour enregistrer dans la galerie
            Button(
                onClick = {
                    isSaving = true
                    scope.launch(Dispatchers.IO) {
                        val success = saveFileToGallery(context, file)
                        isSaving = false
                        scope.launch {
                            Toast.makeText(
                                context,
                                if (success) "Image enregistrée dans la galerie"
                                else "Échec de l'enregistrement",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                },
                enabled = !isSaving,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Enregistrer dans la galerie")
                }
            }
        }
    }
}

/**
 * Compresse le fichier image donné avec la librairie id.zelory:compressor.
 * Ajustez resolution/quality/size selon vos besoins.
 */
suspend fun compressImage(context: android.content.Context, file: File): File {
    return Compressor.compress(context, file) {
        resolution(1280, 1280)
        quality(80)
        format(Bitmap.CompressFormat.JPEG)
        size(1_024_000) // taille cible ~1 Mo
    }
}
