package com.example.drugidentifier

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.drugidentifier.models.DrugInteraction

class InteractionsAdapter(
    private var interactions: List<DrugInteraction>
) : RecyclerView.Adapter<InteractionsAdapter.InteractionViewHolder>() {

    class InteractionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val drug1Name: TextView = view.findViewById(R.id.drug1_name)
        val drug2Name: TextView = view.findViewById(R.id.drug2_name)
        val severityBadge: TextView = view.findViewById(R.id.severity_badge)
        val description: TextView = view.findViewById(R.id.interaction_description)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InteractionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_interaction, parent, false)
        return InteractionViewHolder(view)
    }

    override fun onBindViewHolder(holder: InteractionViewHolder, position: Int) {
        val interaction = interactions[position]
        
        holder.drug1Name.text = interaction.drug1
        holder.drug2Name.text = interaction.drug2
        holder.severityBadge.text = InteractionChecker.getSeverityText(interaction.severity)
        holder.description.text = interaction.description

        val color = ContextCompat.getColor(
            holder.itemView.context,
            InteractionChecker.getSeverityColor(interaction.severity)
        )
        holder.severityBadge.background.setTint(color)
    }

    override fun getItemCount() = interactions.size

    fun updateInteractions(newInteractions: List<DrugInteraction>) {
        interactions = newInteractions
        notifyDataSetChanged()
    }
}
