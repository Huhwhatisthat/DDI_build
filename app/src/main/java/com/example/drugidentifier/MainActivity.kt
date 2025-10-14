package com.example.drugidentifier // Make sure this matches your package name

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
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
import com.google.android.material.button.MaterialButton
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.example.drugidentifier.data.DrugRepository
import kotlinx.coroutines.launch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    private lateinit var cameraExecutor: ExecutorService
    private lateinit var previewView: PreviewView
    private lateinit var resultTextView: TextView
    private lateinit var captureButton: MaterialButton
    private var imageCapture: ImageCapture? = null
    private var nicknameForScan: String? = null

    // Using the shared repository instead of local storage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize the repository
        DrugRepository.init(this)

        previewView = findViewById(R.id.camera_preview)
        resultTextView = findViewById(R.id.result_text)
        captureButton = findViewById(R.id.capture_button)

        // On startup, the button is for adding a new drug.
        captureButton.text = "Add New Drug"
        captureButton.setOnClickListener { showAddDrugDialog() }

        // Hide camera preview initially
        previewView.visibility = View.GONE

        cameraExecutor = Executors.newSingleThreadExecutor()

        // Request camera permissions
        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }
        
        // Handle intent from HomeActivity
        handleIncomingAction()
    }
    
    private fun handleIncomingAction() {
        val action = intent.getStringExtra("ACTION")
        when (action) {
            "SCAN_DRUG", "ADD_DRUG" -> {
                // Both actions start with adding a drug dialog
                showAddDrugDialog()
            }
            "VIEW_DRUGS" -> {
                // Display current drugs
                displayCurrentDrugs()
            }
            "CHECK_INTERACTIONS" -> {
                // Check interactions for current drugs
                checkCurrentInteractions()
            }
        }
    }
    
    private fun displayCurrentDrugs() {
        if (DrugRepository.isEmpty()) {
            resultTextView.text = "No drugs saved yet.\n\nTap 'Add New Drug' to get started."
        } else {
            val drugListText = DrugRepository.getAllDrugs().joinToString("\n") { "- ${it.first} (${it.second})" }
            resultTextView.text = "Your Saved Drugs:\n\n$drugListText"
        }
    }
    
    private fun checkCurrentInteractions() {
        if (DrugRepository.isEmpty()) {
            resultTextView.text = "No drugs to check.\n\nPlease add at least 2 drugs to check for interactions."
            return
        }
        
        if (DrugRepository.size() < 2) {
            resultTextView.text = "You need at least 2 drugs to check for interactions.\n\nCurrent drugs: ${DrugRepository.size()}"
            return
        }
        
        val allIngredients = DrugRepository.getAllIngredients()
        lifecycleScope.launch {
            resultTextView.text = getString(R.string.checking_for_interactions)
            val interactionResult = DrugApiClient.checkInteractions(allIngredients)
            val drugListText = DrugRepository.getAllDrugs().joinToString("\n") { "- ${it.first} (${it.second})" }
            val warningText = interactionResult ?: getString(R.string.no_interactions_found)

            resultTextView.text = "Your Drugs:\n$drugListText\n\n$warningText"
        }
    }

    private fun showAddDrugDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Add a New Drug")
        builder.setMessage("What do you call this medicine? (e.g., Headache Pill)")

        val input = EditText(this)
        builder.setView(input)

        builder.setPositiveButton("Next") { _, _ ->
            val nickname = input.text.toString()
            if (nickname.isNotBlank()) {
                startIngredientScan(nickname)
            } else {
                Toast.makeText(this, "Please enter a name for your drug.", Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
        builder.show()
    }

    private fun startIngredientScan(nickname: String) {
        this.nicknameForScan = nickname

        previewView.visibility = View.VISIBLE
        captureButton.text = "Take Picture"
        captureButton.setOnClickListener { takePhoto() }
        resultTextView.text = "Scan the active ingredient for: $nickname"

        startCamera()
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return
        val nickname = nicknameForScan ?: return

        imageCapture.takePicture(
            ContextCompat.getMainExecutor(this),
            @OptIn(ExperimentalGetImage::class)
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onCaptureSuccess(imageProxy: ImageProxy) {
                    val mediaImage = imageProxy.image
                    if (mediaImage != null) {
                        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                        processImageForIngredient(image, imageProxy, nickname)
                    }
                }

                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                }
            }
        )
    }

    private fun processImageForIngredient(image: InputImage, imageProxy: ImageProxy, nickname: String) {
        TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
            .process(image)
            .addOnSuccessListener { visionText ->
                imageProxy.close()
                val ingredients = extractIngredients(visionText.text)

                if (ingredients.isNotEmpty()) {
                    val foundIngredient = ingredients.first()
                    showConfirmationDialog(nickname, foundIngredient)
                } else {
                    Toast.makeText(baseContext, "Could not find a known ingredient. Please try again.", Toast.LENGTH_LONG).show()
                    resetToInitialState()
                }
            }
            .addOnFailureListener { e ->
                imageProxy.close()
                Log.e(TAG, "Text recognition failed", e)
                resetToInitialState()
            }
    }

    private fun showConfirmationDialog(nickname: String, ingredient: String) {
        AlertDialog.Builder(this)
            .setTitle("Save this Drug?")
            .setMessage("Your Name: $nickname\nIngredient Found: $ingredient")
            .setPositiveButton("Save") { _, _ ->
                saveDrugToDatabase(nickname, ingredient)
            }
            .setNegativeButton("Cancel") { _, _ -> resetToInitialState() }
            .setOnCancelListener { resetToInitialState() }
            .show()
    }

    private fun saveDrugToDatabase(nickname: String, ingredient: String) {
        DrugRepository.addDrug(nickname, ingredient)
        Toast.makeText(this, "$nickname ($ingredient) saved!", Toast.LENGTH_SHORT).show()

        val allIngredients = DrugRepository.getAllIngredients()

        lifecycleScope.launch {
            resultTextView.text = getString(R.string.checking_for_interactions)
            val interactionResult = DrugApiClient.checkInteractions(allIngredients)
            val drugListText = DrugRepository.getAllDrugs().joinToString("\n") { "- ${it.first} (${it.second})" }
            val warningText = interactionResult ?: getString(R.string.no_interactions_found)

            resultTextView.text = "Your Drugs:\n$drugListText\n\n$warningText"
        }

        resetToInitialState()
    }
    
    private fun resetToInitialState() {
        previewView.visibility = View.GONE
        captureButton.text = "Add New Drug"
        captureButton.setOnClickListener { showAddDrugDialog() }
        nicknameForScan = null
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
                val correctedWord = commonMisspellings[word]!!
                foundIngredients.add(correctedWord)
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
                // Don't start camera automatically anymore
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

fun levenshtein(lhs: String, rhs: String): Int {
    val lhsLength = lhs.length
    val rhsLength = rhs.length
    var cost = Array(lhsLength + 1) { it }
    var newCost = Array(lhsLength + 1) { 0 }

    for (i in 1..rhsLength) {
        newCost[0] = i
        for (j in 1..lhsLength) {
            val match = if (lhs[j - 1] == rhs[i - 1]) 0 else 1
            val costReplace = cost[j - 1] + match
            val costInsert = cost[j] + 1
            val costDelete = newCost[j - 1] + 1
            newCost[j] = minOf(costReplace, costInsert, costDelete)
        }
        cost = newCost.clone()
    }
    return cost[lhsLength]
}