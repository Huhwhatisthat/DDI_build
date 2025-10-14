package com.example.drugidentifier

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Set up action cards
        setupActionCards()
    }

    private fun setupActionCards() {
        // Scan Drug action
        val scanDrugCard = findViewById<CardView>(R.id.card_scan_drug)
        scanDrugCard.setOnClickListener {
            // Navigate to MainActivity for scanning
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("ACTION", "SCAN_DRUG")
            startActivity(intent)
        }

        // Add Drug Manually action
        val addDrugCard = findViewById<CardView>(R.id.card_add_drug)
        addDrugCard.setOnClickListener {
            // Navigate to MainActivity for adding drug
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("ACTION", "ADD_DRUG")
            startActivity(intent)
        }

        // View My Drugs action
        val viewDrugsCard = findViewById<CardView>(R.id.card_view_drugs)
        viewDrugsCard.setOnClickListener {
            // Navigate to MainActivity to view drugs
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("ACTION", "VIEW_DRUGS")
            startActivity(intent)
        }

        // Check Interactions action
        val checkInteractionsCard = findViewById<CardView>(R.id.card_check_interactions)
        checkInteractionsCard.setOnClickListener {
            // Navigate to MainActivity to check interactions
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("ACTION", "CHECK_INTERACTIONS")
            startActivity(intent)
        }
    }
}
