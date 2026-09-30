package com.example.drugidentifier

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.drugidentifier.models.Drug
import com.example.drugidentifier.models.DrugInteraction

class DrugListViewAdapter(
    private var drugs: List<Drug>,
    private var interactions: List<DrugInteraction>,
    private var showInteractions: Boolean,
    private val onDeleteClick: (String) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_DRUG = 0
        private const val VIEW_TYPE_INTERACTION = 1
        private const val VIEW_TYPE_INTERACTION_HEADER = 2
    }

    class DrugViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val drugName: TextView = view.findViewById(R.id.drug_name)
        val drugIngredient: TextView = view.findViewById(R.id.drug_ingredient)
        val deleteButton: ImageView = view.findViewById(R.id.delete_button)
    }

    class InteractionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val drug1Name: TextView = view.findViewById(R.id.drug1_name)
        val drug2Name: TextView = view.findViewById(R.id.drug2_name)
        val severityBadge: TextView = view.findViewById(R.id.severity_badge)
        val description: TextView = view.findViewById(R.id.interaction_description)
    }

    class InteractionHeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val headerText: TextView = view.findViewById(R.id.header_text)
    }

    override fun getItemViewType(position: Int): Int {
        if (!showInteractions || interactions.isEmpty()) {
            return VIEW_TYPE_DRUG
        }

        return when {
            position < drugs.size -> VIEW_TYPE_DRUG
            position == drugs.size -> VIEW_TYPE_INTERACTION_HEADER
            else -> VIEW_TYPE_INTERACTION
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_DRUG -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_drug_list, parent, false)
                DrugViewHolder(view)
            }
            VIEW_TYPE_INTERACTION_HEADER -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_interaction_header, parent, false)
                InteractionHeaderViewHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_interaction, parent, false)
                InteractionViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is DrugViewHolder -> {
                val drug = drugs[position]
                holder.drugName.text = drug.name
                holder.drugIngredient.text = drug.activeIngredient
                holder.deleteButton.setOnClickListener {
                    onDeleteClick(drug.name)
                }
            }
            is InteractionHeaderViewHolder -> {
                holder.headerText.text = if (interactions.isEmpty()) {
                    "✓ No Interactions Found"
                } else {
                    "⚠ ${interactions.size} Potential Interaction${if (interactions.size > 1) "s" else ""} Detected"
                }
            }
            is InteractionViewHolder -> {
                val interaction = interactions[position - drugs.size - 1]
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
        }
    }

    override fun getItemCount(): Int {
        return if (!showInteractions || interactions.isEmpty()) {
            drugs.size
        } else {
            drugs.size + 1 + interactions.size
        }
    }

    fun updateData(newDrugs: List<Drug>, newInteractions: List<DrugInteraction>, showInteractions: Boolean) {
        this.drugs = newDrugs
        this.interactions = newInteractions
        this.showInteractions = showInteractions
        notifyDataSetChanged()
    }

    fun updateDrugs(newDrugs: List<Drug>) {
        updateData(newDrugs, emptyList(), false)
    }
}
