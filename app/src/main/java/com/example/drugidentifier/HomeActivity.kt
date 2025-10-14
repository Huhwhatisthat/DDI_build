package com.example.drugidentifier

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.drugidentifier.data.DrugRepository
import com.example.drugidentifier.models.Drug
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class HomeActivity : AppCompatActivity() {

    private lateinit var drugsRecyclerView: RecyclerView
    private lateinit var emptyState: LinearLayout
    private lateinit var drugListAdapter: DrugListAdapter
    
    private lateinit var prescriptionsSection: LinearLayout
    private lateinit var prescriptionsRecyclerView: RecyclerView
    private lateinit var prescriptionAdapter: PrescriptionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Initialize the repository
        DrugRepository.init(this)

        // Set up RecyclerView
        setupRecyclerView()
        
        // Set up prescriptions section
        setupPrescriptionsSection()

        // Set up action cards
        setupActionCards()
    }

    override fun onResume() {
        super.onResume()
        // Refresh the drug list when returning to this activity
        updateDrugList()
        updatePrescriptionsList()
    }

    private fun setupRecyclerView() {
        drugsRecyclerView = findViewById(R.id.drugs_recycler_view)
        emptyState = findViewById(R.id.empty_state)

        drugListAdapter = DrugListAdapter { drug ->
            showDeleteConfirmation(drug)
        }

        drugsRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@HomeActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = drugListAdapter
        }

        updateDrugList()
    }
    
    private fun setupPrescriptionsSection() {
        prescriptionsSection = findViewById(R.id.prescriptions_section)
        prescriptionsRecyclerView = findViewById(R.id.prescriptions_recycler)
        
        prescriptionsRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@HomeActivity)
            setHasFixedSize(false)
        }
        
        updatePrescriptionsList()
    }
    
    private fun updatePrescriptionsList() {
        val todaysPrescriptions = DrugRepository.getTodaysPrescriptions()
        
        if (todaysPrescriptions.isEmpty()) {
            prescriptionsSection.visibility = View.GONE
        } else {
            prescriptionsSection.visibility = View.VISIBLE
            prescriptionAdapter = PrescriptionAdapter(todaysPrescriptions) { drug ->
                showMedicationStatusDialog(drug)
            }
            prescriptionsRecyclerView.adapter = prescriptionAdapter
        }
    }
    
    private fun showMedicationStatusDialog(drug: Drug) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_medication_status, null)
        
        val dialog = MaterialAlertDialogBuilder(this)
            .setView(dialogView)
            .setCancelable(true)
            .create()
        
        // Set drug info
        dialogView.findViewById<TextView>(R.id.dialog_drug_name).text = drug.name
        val detailsText = "${drug.quantity} ${if (drug.quantity == 1) "pill" else "pills"} • ${drug.time}"
        dialogView.findViewById<TextView>(R.id.dialog_drug_details).text = detailsText
        
        // Taken button
        dialogView.findViewById<LinearLayout>(R.id.btn_taken).setOnClickListener {
            DrugRepository.updateMedicationStatus(drug.name, taken = true)
            updatePrescriptionsList()
            dialog.dismiss()
        }
        
        // Skipped button
        dialogView.findViewById<LinearLayout>(R.id.btn_skipped).setOnClickListener {
            DrugRepository.updateMedicationStatus(drug.name, taken = false)
            updatePrescriptionsList()
            dialog.dismiss()
        }
        
        // Cancel button
        dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.btn_cancel).setOnClickListener {
            dialog.dismiss()
        }
        
        dialog.show()
    }

    private fun updateDrugList() {
        val drugs = DrugRepository.getAllDrugs().toList()
        
        if (drugs.isEmpty()) {
            drugsRecyclerView.visibility = View.GONE
            emptyState.visibility = View.VISIBLE
        } else {
            drugsRecyclerView.visibility = View.VISIBLE
            emptyState.visibility = View.GONE
            drugListAdapter.updateDrugs(drugs)
        }
    }

    private fun showDeleteConfirmation(drug: Pair<String, String>) {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.delete_drug_confirmation, drug.first))
            .setMessage("${drug.first}\n${drug.second}")
            .setPositiveButton(getString(R.string.delete)) { _, _ ->
                DrugRepository.removeDrug(drug.first, drug.second)
                updateDrugList()
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }

    private fun setupActionCards() {
        // Add Drug action (merged scan + manual)
        val addDrugCard = findViewById<LinearLayout>(R.id.card_add_drug)
        addDrugCard.setOnClickListener {
            // Navigate to new AddDrugActivity
            val intent = Intent(this, AddDrugActivity::class.java)
            startActivity(intent)
        }

        // View My Drugs action
        val viewDrugsCard = findViewById<LinearLayout>(R.id.card_view_drugs)
        viewDrugsCard.setOnClickListener {
            // Navigate to ViewDrugsActivity
            val intent = Intent(this, ViewDrugsActivity::class.java)
            startActivity(intent)
        }
    }
}
