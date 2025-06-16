package com.example.scantranslate

import android.Manifest
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.TranslatorOptions
import com.google.mlkit.nl.translate.Translation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MainActivity : ComponentActivity() {

    private val history = mutableListOf<Pair<String,String>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.CAMERA) !=
            android.content.pm.PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), 101)
        }

        setContent {
            ScanTranslateScreen()
        }
    }

    @Composable
    fun ScanTranslateScreen() {
        var imageBmp by remember { mutableStateOf<Bitmap?>(null) }
        var extractedText by remember { mutableStateOf("") }
        var translatedText by remember { mutableStateOf("") }
        var targetLang by remember { mutableStateOf("es") }

        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Text("Scan & Translate", style = MaterialTheme.typography.headlineMedium)

            Spacer(modifier = Modifier.height(12.dp))

            Box(modifier = Modifier
                .height(200.dp)
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primaryContainer)
                .clickable { },
                contentAlignment = Alignment.Center
            ) {
                Text("Tap to capture (stub)", textAlign = TextAlign.Center)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(onClick = {
                captureAndProcess { img, orig -> 
                    imageBmp = img
                    extractedText = orig
                    translateText(orig, targetLang) { tr ->
                        translatedText = tr
                        history.add(orig to tr)
                    }
                }
            }) {
                Text("Scan Text & Translate to $targetLang")
            }

            Spacer(modifier = Modifier.height(12.dp))

            imageBmp?.let {
                AndroidView(factory = { ctx ->
                    androidx.compose.foundation.Image(bitmap = it.asImageBitmap(), contentDescription = null)
                })
            }

            if (extractedText.isNotEmpty()) {
                SelectionContainer { Text("OCR: $extractedText") }
            }

            if (translatedText.isNotEmpty()) {
                Text("Translated: $translatedText", style = MaterialTheme.typography.bodyLarge)
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text("History", style = MaterialTheme.typography.headlineSmall)
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(history.size) { idx ->
                    val (o,t) = history[idx]
                    Card(modifier = Modifier
                        .padding(vertical = 4.dp)
                        .fillMaxWidth()) {
                        Column(modifier=Modifier.padding(8.dp)) {
                            Text("OCR: $o", maxLines = 1)
                            Text("→ $t", style=MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }
    }

    private fun captureAndProcess(callback: (Bitmap, String)->Unit) {
        lifecycleScope.launch(Dispatchers.IO) {
            val fakeImage = Bitmap.createBitmap(400,200,Bitmap.Config.ARGB_8888)
            val recognized = "Hello World"
            launch(Dispatchers.Main) { callback(fakeImage, recognized) }
        }
    }

    private fun translateText(text: String, lang: String, onDone: (String)->Unit) {
        lifecycleScope.launch {
            val options = TranslatorOptions.Builder()
                .setSourceLanguage(TranslateLanguage.ENGLISH)
                .setTargetLanguage(lang)
                .build()
            val translator = Translation.getClient(options)
            translator.downloadModelIfNeeded().await()
            val translated = translator.translate(text).await()
            onDone(translated)
        }
    }
}