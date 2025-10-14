package com.example.drugidentifier

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.drugidentifier.models.Drug

class DrugListViewAdapter(
    private var drugs: List<Drug>,
    private val onDeleteClick: (String) -> Unit
) : RecyclerView.Adapter<DrugListViewAdapter.DrugViewHolder>() {

    class DrugViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val drugName: TextView = view.findViewById(R.id.drug_name)
        val drugIngredient: TextView = view.findViewById(R.id.drug_ingredient)
        val deleteButton: ImageView = view.findViewById(R.id.delete_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DrugViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_drug_list, parent, false)
        return DrugViewHolder(view)
    }

    override fun onBindViewHolder(holder: DrugViewHolder, position: Int) {
        val drug = drugs[position]
        
        holder.drugName.text = drug.name
        holder.drugIngredient.text = drug.activeIngredient
        
        holder.deleteButton.setOnClickListener {
            onDeleteClick(drug.name)
        }
    }

    override fun getItemCount() = drugs.size

    fun updateDrugs(newDrugs: List<Drug>) {
        drugs = newDrugs
        notifyDataSetChanged()
    }
}
