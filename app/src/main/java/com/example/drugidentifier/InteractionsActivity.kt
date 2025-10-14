package com.example.drugidentifier

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.drugidentifier.data.DrugRepository

class InteractionsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyStateContainer: View
    private lateinit var interactionCount: TextView
    private lateinit var adapter: InteractionsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_interactions)

        DrugRepository.init(this)

        // Initialize views
        val backButton = findViewById<ImageView>(R.id.back_button)
        recyclerView = findViewById(R.id.interactions_recycler_view)
        emptyStateContainer = findViewById(R.id.empty_state_container)
        interactionCount = findViewById(R.id.interaction_count)

        // Back button
        backButton.setOnClickListener {
            finish()
        }

        // Setup RecyclerView
        setupRecyclerView()

        // Load interactions
        loadInteractions()
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = InteractionsAdapter(emptyList())
        recyclerView.adapter = adapter
    }

    private fun loadInteractions() {
        val drugs = DrugRepository.getDrugsList()
        
        if (drugs.size < 2) {
            // Show empty state - need at least 2 drugs
            recyclerView.visibility = View.GONE
            emptyStateContainer.visibility = View.VISIBLE
            interactionCount.text = "0"
        } else {
            // Check for interactions
            val ingredients = drugs.map { it.activeIngredient }
            val interactions = InteractionChecker.checkInteractions(ingredients)
            
            if (interactions.isEmpty()) {
                // No interactions found - show empty state
                recyclerView.visibility = View.GONE
                emptyStateContainer.visibility = View.VISIBLE
                interactionCount.text = "0"
            } else {
                // Show interactions
                recyclerView.visibility = View.VISIBLE
                emptyStateContainer.visibility = View.GONE
                interactionCount.text = interactions.size.toString()
                adapter.updateInteractions(interactions)
            }
        }
    }
}
