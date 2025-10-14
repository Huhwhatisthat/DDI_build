package com.example.drugidentifier

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.drugidentifier.data.DrugRepository
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.launch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class AddDrugActivity : AppCompatActivity() {

    private lateinit var backButton: ImageView
    private lateinit var nicknameInput: TextInputEditText
    private lateinit var ingredientInput: TextInputEditText
    private lateinit var scanOptionCard: LinearLayout
    private lateinit var manualOptionCard: LinearLayout
    private lateinit var cameraSection: LinearLayout
    private lateinit var manualEntrySection: LinearLayout
    private lateinit var cameraPreview: PreviewView
    private lateinit var captureButton: MaterialButton
    private lateinit var cancelScanButton: MaterialButton
    private lateinit var saveManualButton: MaterialButton
    private lateinit var cancelManualButton: MaterialButton

    private var imageCapture: ImageCapture? = null
    private lateinit var cameraExecutor: ExecutorService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_drug)

        DrugRepository.init(this)
        cameraExecutor = Executors.newSingleThreadExecutor()

        initializeViews()
        setupListeners()
    }

    private fun initializeViews() {
        backButton = findViewById(R.id.back_button)
        nicknameInput = findViewById(R.id.nickname_input)
        ingredientInput = findViewById(R.id.ingredient_input)
        scanOptionCard = findViewById(R.id.scan_option_card)
        manualOptionCard = findViewById(R.id.manual_option_card)
        cameraSection = findViewById(R.id.camera_section)
        manualEntrySection = findViewById(R.id.manual_entry_section)
        cameraPreview = findViewById(R.id.camera_preview)
        captureButton = findViewById(R.id.capture_button)
        cancelScanButton = findViewById(R.id.cancel_scan_button)
        saveManualButton = findViewById(R.id.save_manual_button)
        cancelManualButton = findViewById(R.id.cancel_manual_button)
    }

    private fun setupListeners() {
        backButton.setOnClickListener { finish() }

        scanOptionCard.setOnClickListener {
            val nickname = nicknameInput.text.toString().trim()
            if (nickname.isEmpty()) {
                Toast.makeText(this, "Please enter medication name first", Toast.LENGTH_SHORT).show()
                nicknameInput.requestFocus()
                return@setOnClickListener
            }
            showScanSection()
        }

        manualOptionCard.setOnClickListener {
            val nickname = nicknameInput.text.toString().trim()
            if (nickname.isEmpty()) {
                Toast.makeText(this, "Please enter medication name first", Toast.LENGTH_SHORT).show()
                nicknameInput.requestFocus()
                return@setOnClickListener
            }
            showManualEntrySection()
        }

        captureButton.setOnClickListener { takePhoto() }
        cancelScanButton.setOnClickListener { hideScanSection() }

        saveManualButton.setOnClickListener { saveManualEntry() }
        cancelManualButton.setOnClickListener { hideManualEntrySection() }
    }

    private fun showScanSection() {
        // Request camera permissions if needed
        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
            return
        }

        // Hide option cards
        scanOptionCard.visibility = View.GONE
        manualOptionCard.visibility = View.GONE

        // Show camera section
        cameraSection.visibility = View.VISIBLE
        startCamera()
    }

    private fun hideScanSection() {
        cameraSection.visibility = View.GONE
        scanOptionCard.visibility = View.VISIBLE
        manualOptionCard.visibility = View.VISIBLE
    }

    private fun showManualEntrySection() {
        // Hide option cards
        scanOptionCard.visibility = View.GONE
        manualOptionCard.visibility = View.GONE

        // Show manual entry section
        manualEntrySection.visibility = View.VISIBLE
        ingredientInput.requestFocus()
    }

    private fun hideManualEntrySection() {
        manualEntrySection.visibility = View.GONE
        scanOptionCard.visibility = View.VISIBLE
        manualOptionCard.visibility = View.VISIBLE
        ingredientInput.setText("")
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(cameraPreview.surfaceProvider)
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

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return
        val nickname = nicknameInput.text.toString().trim()

        imageCapture.takePicture(
            ContextCompat.getMainExecutor(this),
            @OptIn(ExperimentalGetImage::class)
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onCaptureSuccess(imageProxy: ImageProxy) {
                    val mediaImage = imageProxy.image
                    if (mediaImage != null) {
                        val image = InputImage.fromMediaImage(
                            mediaImage,
                            imageProxy.imageInfo.rotationDegrees
                        )
                        processImageForIngredient(image, imageProxy, nickname)
                    }
                }

                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                    Toast.makeText(
                        baseContext,
                        "Failed to capture image",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        )
    }

    private fun processImageForIngredient(
        image: InputImage,
        imageProxy: ImageProxy,
        nickname: String
    ) {
        TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
            .process(image)
            .addOnSuccessListener { visionText ->
                imageProxy.close()
                val ingredients = extractIngredients(visionText.text)

                if (ingredients.isNotEmpty()) {
                    val foundIngredient = ingredients.first()
                    showModernConfirmationDialog(nickname, foundIngredient)
                } else {
                    Toast.makeText(
                        baseContext,
                        "No ingredient found. Please try again or enter manually.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
            .addOnFailureListener { e ->
                imageProxy.close()
                Log.e(TAG, "Text recognition failed", e)
                Toast.makeText(
                    baseContext,
                    "Scan failed. Please try again.",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun showModernConfirmationDialog(nickname: String, ingredient: String) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_confirm_drug, null)
        
        val dialog = MaterialAlertDialogBuilder(this)
            .setView(dialogView)
            .setCancelable(true)
            .create()

        dialogView.findViewById<com.google.android.material.textview.MaterialTextView>(R.id.dialog_medication_name).text = nickname
        dialogView.findViewById<com.google.android.material.textview.MaterialTextView>(R.id.dialog_ingredient_name).text = ingredient

        dialogView.findViewById<MaterialButton>(R.id.dialog_save_button).setOnClickListener {
            saveDrug(nickname, ingredient)
            dialog.dismiss()
        }

        dialogView.findViewById<MaterialButton>(R.id.dialog_cancel_button).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun saveManualEntry() {
        val nickname = nicknameInput.text.toString().trim()
        val ingredient = ingredientInput.text.toString().trim()

        if (ingredient.isEmpty()) {
            Toast.makeText(this, "Please enter the active ingredient", Toast.LENGTH_SHORT).show()
            ingredientInput.requestFocus()
            return
        }

        showModernConfirmationDialog(nickname, ingredient)
    }

    private fun saveDrug(nickname: String, ingredient: String) {
        DrugRepository.addDrug(nickname, ingredient)
        Toast.makeText(this, "✓ $nickname saved successfully!", Toast.LENGTH_SHORT).show()

        // Check interactions in background
        val allIngredients = DrugRepository.getAllIngredients()
        lifecycleScope.launch {
            try {
                val interactionResult = DrugApiClient.checkInteractions(allIngredients)
                if (interactionResult != null && !interactionResult.contains("No interactions")) {
                    showInteractionWarning(interactionResult)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to check interactions", e)
            }
        }

        finish()
    }

    private fun showInteractionWarning(warning: String) {
        MaterialAlertDialogBuilder(this)
            .setTitle("⚠️ Interaction Warning")
            .setMessage(warning)
            .setPositiveButton("OK", null)
            .show()
    }

    private fun extractIngredients(rawText: String): List<String> {
        val knownIngredients = setOf(
            "paracetamol", "ibuprofen", "aspirin", "caffeine", "naproxen",
            "cetirizine", "loratadine", "diphenhydramine", "guaifenesin",
            "pseudoephedrine", "doxycycline", "calcium carbonate"
        )
        val commonMisspellings = mapOf(
            "ibuproten" to "ibuprofen",
            "lbuprofen" to "ibuprofen",
            "paracetamo1" to "paracetamol"
        )
        val cleanedText = rawText.lowercase()
            .replace(Regex("\\d+\\s*(mg|g)"), " ")
            .replace(Regex("[.,:;()]"), " ")
        val words = cleanedText.split(Regex("\\s+")).filter { it.isNotBlank() && it.length > 3 }
        val foundIngredients = mutableSetOf<String>()
        for (word in words) {
            if (commonMisspellings.containsKey(word)) {
                foundIngredients.add(commonMisspellings[word]!!)
                continue
            }
            if (word in knownIngredients) {
                foundIngredients.add(word)
                continue
            }
            var bestMatch: String? = null
            var minDistance = 3
            for (known in knownIngredients) {
                val distance = levenshtein(word, known)
                if (distance < minDistance) {
                    minDistance = distance
                    bestMatch = known
                }
            }
            if (bestMatch != null) {
                foundIngredients.add(bestMatch)
            }
        }
        return foundIngredients.toList()
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                showScanSection()
            } else {
                Toast.makeText(
                    this,
                    "Camera permission is required to scan",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    companion object {
        private const val TAG = "AddDrugActivity"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }
}
