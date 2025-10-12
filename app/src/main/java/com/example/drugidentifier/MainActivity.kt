package com.example.drugidentifier // Make sure this matches your package name

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.launch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    private lateinit var cameraExecutor: ExecutorService
    private lateinit var previewView: PreviewView
    private lateinit var resultTextView: TextView
    private var imageCapture: ImageCapture? = null

    // This Set will store all the ingredients scanned so far.
    private val currentIngredients = mutableSetOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        previewView = findViewById(R.id.camera_preview)
        resultTextView = findViewById(R.id.result_text)
        val captureButton: Button = findViewById(R.id.capture_button)

        // Request camera permissions
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }

        captureButton.setOnClickListener { takePhoto() }
        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return

        imageCapture.takePicture(
            ContextCompat.getMainExecutor(this),
            @OptIn(ExperimentalGetImage::class)
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onCaptureSuccess(imageProxy: ImageProxy) {
                    val mediaImage = imageProxy.image
                    if (mediaImage != null) {
                        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                        processImageWithMLKit(image, imageProxy)
                    }
                }

                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                }
            }
        )
    }

    private fun processImageWithMLKit(image: InputImage, imageProxy: ImageProxy) {
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        recognizer.process(image)
            .addOnSuccessListener { visionText ->
                val rawOcrText = visionText.text
                val ingredients = extractIngredients(rawOcrText)

                if (ingredients.isNotEmpty()) {
                    currentIngredients.addAll(ingredients)
                    val capitalizedIngredients = ingredients.map { it.replaceFirstChar(Char::titlecase) }
                    Toast.makeText(this, getString(R.string.ingredients_added, capitalizedIngredients.joinToString()), Toast.LENGTH_SHORT).show()

                    // Use a coroutine to check for interactions in the background
                    lifecycleScope.launch {
                        resultTextView.text = getString(R.string.checking_for_interactions)
                        val interactionResult = DrugApiClient.checkInteractions(currentIngredients.toList())
                        val capitalizedCurrentIngredients = currentIngredients.map { it.replaceFirstChar(Char::titlecase) }
                        val fullListText = getString(R.string.current_drugs, capitalizedCurrentIngredients.joinToString())
                        val warningText = interactionResult ?: getString(R.string.no_interactions_found)

                        resultTextView.text = "$fullListText\n$warningText"
                    }
                } else {
                    Toast.makeText(this, getString(R.string.no_known_ingredients_found), Toast.LENGTH_SHORT).show()
                }

                imageProxy.close()
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Text recognition failed", e)
                Toast.makeText(baseContext, "Failed to recognize text.", Toast.LENGTH_SHORT).show()
                imageProxy.close()
            }
    }

    /**
     * Parses raw OCR text to extract a list of known active ingredients.
     */
    private fun extractIngredients(rawText: String): List<String> {
        val knownIngredients = setOf(
            "paracetamol", "ibuprofen", "aspirin", "caffeine", "naproxen",
            "cetirizine", "loratadine", "diphenhydramine", "guaifenesin", "serratiopeptidase", "warfarin"
        )

        val normalizedText = rawText.lowercase()
        val cleanedText = normalizedText
            .replace(Regex("\\d+\\s*(mg|g)"), " ")
            .replace(Regex("[.,:;()]"), " ")

        val words = cleanedText.split(Regex("\\s+")).filter { it.isNotBlank() }
        val foundIngredients = mutableSetOf<String>()

        for (word in words) {
            if (word in knownIngredients) {
                foundIngredients.add(word)
            }
        }
        return foundIngredients.toList()
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            imageCapture = ImageCapture.Builder().build()
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(this, "Permissions not granted by the user.", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    companion object {
        private const val TAG = "DrugIdentifier"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }
}