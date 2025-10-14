package com.example.drugidentifier

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.drugidentifier.data.DrugRepository

class ViewDrugsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyStateContainer: View
    private lateinit var drugCountBadge: TextView
    private lateinit var checkInteractionsButton: LinearLayout
    private lateinit var adapter: DrugListViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_drugs)

        DrugRepository.init(this)

        // Initialize views
        val backButton = findViewById<ImageView>(R.id.back_button)
        recyclerView = findViewById(R.id.drugs_list)
        emptyStateContainer = findViewById(R.id.empty_state)
        drugCountBadge = findViewById(R.id.drug_count)
        checkInteractionsButton = findViewById(R.id.check_interactions_button)

        // Back button
        backButton.setOnClickListener {
            finish()
        }

        // Setup RecyclerView
        setupRecyclerView()

        // Setup interactions button - navigate to interactions page
        checkInteractionsButton.setOnClickListener {
            val intent = Intent(this, InteractionsActivity::class.java)
            startActivity(intent)
        }

        // Load drugs
        loadDrugs()
    }

    override fun onResume() {
        super.onResume()
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
            checkInteractionsButton.isEnabled = false
            checkInteractionsButton.alpha = 0.5f
        } else {
            // Show drugs list
            recyclerView.visibility = View.VISIBLE
            emptyStateContainer.visibility = View.GONE
            drugCountBadge.text = drugs.size.toString()
            checkInteractionsButton.isEnabled = true
            checkInteractionsButton.alpha = 1.0f
            
            adapter.updateData(drugs, emptyList(), false)
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
