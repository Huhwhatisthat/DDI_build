package com.example.drugidentifier

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.drugidentifier.models.Drug
import java.text.SimpleDateFormat
import java.util.*

/**
 * Adapter for displaying today's prescriptions with status tracking
 */
class PrescriptionAdapter(
    private val prescriptions: List<Drug>
) : RecyclerView.Adapter<PrescriptionAdapter.PrescriptionViewHolder>() {

    class PrescriptionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val statusIcon: ImageView = view.findViewById(R.id.status_icon)
        val drugName: TextView = view.findViewById(R.id.drug_name)
        val quantityText: TextView = view.findViewById(R.id.quantity_text)
        val frequencyText: TextView = view.findViewById(R.id.frequency_text)
        val timeText: TextView = view.findViewById(R.id.time_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PrescriptionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_prescription, parent, false)
        return PrescriptionViewHolder(view)
    }

    override fun onBindViewHolder(holder: PrescriptionViewHolder, position: Int) {
        val drug = prescriptions[position]
        
        holder.drugName.text = drug.name
        
        // Quantity text
        val quantityStr = if (drug.quantity == 1) "1 pill" else "${drug.quantity} pills"
        holder.quantityText.text = quantityStr
        
        holder.frequencyText.text = drug.frequency
        holder.timeText.text = drug.time
        
        // Determine status (Pending or Completed) based on current time
        val isPastTime = isPastScheduledTime(drug.time)
        
        if (isPastTime) {
            // Completed - green check
            holder.statusIcon.setImageResource(R.drawable.ic_check)
            holder.statusIcon.setColorFilter(
                ContextCompat.getColor(holder.itemView.context, R.color.success_color)
            )
        } else {
            // Pending - yellow/orange clock
            holder.statusIcon.setImageResource(R.drawable.ic_pending)
            holder.statusIcon.setColorFilter(
                ContextCompat.getColor(holder.itemView.context, R.color.warning_color)
            )
        }
    }

    override fun getItemCount() = prescriptions.size

    /**
     * Check if the scheduled time has passed
     */
    private fun isPastScheduledTime(scheduledTime: String): Boolean {
        return try {
            val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
            val scheduled = sdf.parse(scheduledTime)
            
            val now = Calendar.getInstance()
            val scheduledCal = Calendar.getInstance()
            scheduledCal.time = scheduled ?: return false
            
            // Set the same date for fair comparison
            scheduledCal.set(Calendar.YEAR, now.get(Calendar.YEAR))
            scheduledCal.set(Calendar.DAY_OF_YEAR, now.get(Calendar.DAY_OF_YEAR))
            
            now.after(scheduledCal)
        } catch (e: Exception) {
            false
        }
    }
}
