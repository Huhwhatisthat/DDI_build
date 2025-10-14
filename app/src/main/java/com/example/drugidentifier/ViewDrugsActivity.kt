package com.example.drugidentifier

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.drugidentifier.data.DrugRepository
import com.google.android.material.materialswitch.MaterialSwitch

class ViewDrugsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyStateContainer: View
    private lateinit var drugCountBadge: TextView
    private lateinit var interactionsSwitch: MaterialSwitch
    private lateinit var adapter: DrugListViewAdapter
    private var showInteractions = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_drugs)

        DrugRepository.init(this)

        // Initialize views
        val backButton = findViewById<ImageView>(R.id.back_button)
        recyclerView = findViewById(R.id.drugs_list)
        emptyStateContainer = findViewById(R.id.empty_state)
        drugCountBadge = findViewById(R.id.drug_count)
        interactionsSwitch = findViewById(R.id.interactions_switch)

        // Back button
        backButton.setOnClickListener {
            finish()
        }

        // Setup RecyclerView
        setupRecyclerView()

        // Setup interactions switch
        interactionsSwitch.setOnCheckedChangeListener { _, isChecked ->
            showInteractions = isChecked
            loadDrugs()
        }

        // Load drugs
        loadDrugs()
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = DrugListViewAdapter(
            drugs = emptyList(),
            interactions = emptyList(),
            showInteractions = false,
            onDeleteClick = { drugName ->
                deleteDrug(drugName)
            }
        )
        recyclerView.adapter = adapter
    }

    private fun loadDrugs() {
        val drugs = DrugRepository.getDrugsList()
        
        if (drugs.isEmpty()) {
            // Show empty state
            recyclerView.visibility = View.GONE
            emptyStateContainer.visibility = View.VISIBLE
            drugCountBadge.text = "0"
            interactionsSwitch.isEnabled = false
        } else {
            // Show drugs list
            recyclerView.visibility = View.VISIBLE
            emptyStateContainer.visibility = View.GONE
            drugCountBadge.text = drugs.size.toString()
            interactionsSwitch.isEnabled = true
            
            // Check for interactions if switch is on
            val interactions = if (showInteractions) {
                val ingredients = drugs.map { it.activeIngredient }
                InteractionChecker.checkInteractions(ingredients)
            } else {
                emptyList()
            }
            
            adapter.updateData(drugs, interactions, showInteractions)
        }
    }

    private fun deleteDrug(drugName: String) {
        // Show confirmation dialog
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Delete Medication")
            .setMessage("Are you sure you want to delete $drugName?")
            .setPositiveButton("Delete") { _, _ ->
                DrugRepository.deleteDrug(drugName)
                loadDrugs() // Refresh list
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
