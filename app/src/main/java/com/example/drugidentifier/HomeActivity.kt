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

class HomeActivity : AppCompatActivity() {

    private lateinit var drugsRecyclerView: RecyclerView
    private lateinit var emptyState: LinearLayout
    private lateinit var drugListAdapter: DrugListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Initialize the repository
        DrugRepository.init(this)

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
            // Navigate to MainActivity to view drugs
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("ACTION", "VIEW_DRUGS")
            startActivity(intent)
        }

        // Check Interactions action
        val checkInteractionsCard = findViewById<LinearLayout>(R.id.card_check_interactions)
        checkInteractionsCard.setOnClickListener {
            // Navigate to MainActivity to check interactions
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("ACTION", "CHECK_INTERACTIONS")
            startActivity(intent)
        }
    }
}
