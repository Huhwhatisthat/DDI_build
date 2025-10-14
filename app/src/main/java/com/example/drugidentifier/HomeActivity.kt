package com.example.drugidentifier

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.drugidentifier.data.DrugRepository

class HomeActivity : AppCompatActivity() {

    private lateinit var drugsRecyclerView: RecyclerView
    private lateinit var emptyState: LinearLayout
    private lateinit var drugListAdapter: DrugListAdapter
    private lateinit var medicationCountText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Initialize the repository
        DrugRepository.init(this)

        // Initialize views
        medicationCountText = findViewById(R.id.medication_count)

        // Set up RecyclerView
        setupRecyclerView()

        // Set up action cards
        setupActionCards()
    }

    override fun onResume() {
        super.onResume()
        // Refresh the drug list when returning to this activity
        updateDrugList()
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

    private fun updateDrugList() {
        val drugs = DrugRepository.getAllDrugs().toList()
        
        // Update medication count
        val count = drugs.size
        medicationCountText.text = when (count) {
            0 -> "You have no medications yet"
            1 -> "You have 1 medication"
            else -> "You have $count medications"
        }
        
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
        AlertDialog.Builder(this, R.style.Theme_DrugIdentifier)
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
        // Add Drug action (with scan option)
        val addDrugCard = findViewById<CardView>(R.id.card_add_drug)
        addDrugCard.setOnClickListener {
            showAddDrugOptions()
        }

        // View My Drugs action (with interactions check)
        val viewDrugsCard = findViewById<CardView>(R.id.card_view_drugs)
        viewDrugsCard.setOnClickListener {
            // Navigate to MainActivity to view drugs with interactions
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("ACTION", "VIEW_DRUGS")
            startActivity(intent)
        }
    }

    private fun showAddDrugOptions() {
        val options = arrayOf("📷  Scan Drug Label", "✍️  Enter Manually")
        
        AlertDialog.Builder(this, R.style.Theme_DrugIdentifier)
            .setTitle("Add Medication")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> {
                        // Scan Drug
                        val intent = Intent(this, MainActivity::class.java)
                        intent.putExtra("ACTION", "SCAN_DRUG")
                        startActivity(intent)
                    }
                    1 -> {
                        // Add Drug Manually
                        val intent = Intent(this, MainActivity::class.java)
                        intent.putExtra("ACTION", "ADD_DRUG")
                        startActivity(intent)
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
