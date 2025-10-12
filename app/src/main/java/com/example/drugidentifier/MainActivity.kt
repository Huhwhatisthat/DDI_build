package com.example.drugidentifier // Make sure this matches your package name

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    private lateinit var cameraExecutor: ExecutorService
    private lateinit var previewView: PreviewView
    private lateinit var resultTextView: TextView
    private var imageCapture: ImageCapture? = null

    // This Set will store all the ingredients scanned so far.
    private val currentIngredients = mutableSetOf<String>()

    /**
     * A simple hardcoded database for our Proof of Concept.
     */
    private val interactionDatabase = mapOf(
        "ibuprofen" to mapOf(
            "aspirin" to "High risk of stomach bleeding. Avoid taking together.",
            "naproxen" to "Both are NSAIDs. Taking them together increases risk of side effects."
        ),
        "aspirin" to mapOf(
            "ibuprofen" to "High risk of stomach bleeding. Avoid taking together."
        ),
        "paracetamol" to mapOf(
            // Paracetamol is quite safe, but let's add a placeholder
            "warfarin" to "Increased risk of bleeding. Consult a doctor."
        )
    )

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
                    // Add the newly found ingredients to our master list
                    currentIngredients.addAll(ingredients)

                    // Check the entire list for interactions
                    val interactionResult = checkPocInteractions(currentIngredients.toList())

                    // Capitalize for display
                    val capitalizedCurrentIngredients = currentIngredients.map { it.replaceFirstChar(Char::titlecase) }
                    val capitalizedIngredients = ingredients.map { it.replaceFirstChar(Char::titlecase) }

                    // Display the complete list and any warnings
                    val fullListText = "Current Drugs: ${capitalizedCurrentIngredients.joinToString()}"
                    val warningText = interactionResult ?: "No interactions found."

                    resultTextView.text = "$fullListText\n$warningText"
                    Toast.makeText(this, "${capitalizedIngredients.joinToString()} added.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "No known ingredients found.", Toast.LENGTH_SHORT).show()
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

    /**
     * Checks a list of ingredients against the hardcoded database.
     */
    private fun checkPocInteractions(ingredients: List<String>): String? {
        if (ingredients.size < 2) return null

        for (i in ingredients.indices) {
            for (j in i + 1 until ingredients.size) {
                val drug1 = ingredients[i]
                val drug2 = ingredients[j]

                // Check for interaction from drug1 to drug2
                interactionDatabase[drug1]?.get(drug2)?.let {
                    return "Interaction: ${drug1.replaceFirstChar(Char::titlecase)} + ${drug2.replaceFirstChar(Char::titlecase)} - $it"
                }
                // Check for interaction from drug2 to drug1
                interactionDatabase[drug2]?.get(drug1)?.let {
                    return "Interaction: ${drug2.replaceFirstChar(Char::titlecase)} + ${drug1.replaceFirstChar(Char::titlecase)} - $it"
                }
            }
        }
        return null // No interactions found
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