package com.example.drugidentifier

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DrugListAdapter(
    private val onDeleteClick: (Pair<String, String>) -> Unit
) : RecyclerView.Adapter<DrugListAdapter.DrugViewHolder>() {

    private var drugs = listOf<Pair<String, String>>()

    fun updateDrugs(newDrugs: List<Pair<String, String>>) {
        drugs = newDrugs
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DrugViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_drug_card, parent, false)
        return DrugViewHolder(view)
    }

    override fun onBindViewHolder(holder: DrugViewHolder, position: Int) {
        holder.bind(drugs[position])
    }

    override fun getItemCount(): Int = drugs.size

    inner class DrugViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nicknameTextView: TextView = itemView.findViewById(R.id.drug_nickname)
        private val ingredientTextView: TextView = itemView.findViewById(R.id.drug_ingredient)
        private val deleteButton: TextView = itemView.findViewById(R.id.delete_button)

        fun bind(drug: Pair<String, String>) {
            nicknameTextView.text = drug.first
            ingredientTextView.text = drug.second
            
            deleteButton.setOnClickListener {
                onDeleteClick(drug)
            }
        }
    }
}
