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
 * 
 * Status States:
 * - UPCOMING (Gray): Scheduled time hasn't arrived yet
 * - PENDING (Yellow): Time arrived, less than 5 minutes passed
 * - MISSED (Red): Time arrived, more than 5 minutes passed, not taken
 * - TAKEN (Green): Marked as taken by user
 */
class PrescriptionAdapter(
    private val prescriptions: List<Drug>,
    private val onItemClick: (Drug) -> Unit
) : RecyclerView.Adapter<PrescriptionAdapter.PrescriptionViewHolder>() {

    enum class MedicationStatus {
        UPCOMING,   // Gray - time hasn't come
        PENDING,    // Yellow - time came, < 5 mins
        MISSED,     // Red - time came, > 5 mins, not taken
        TAKEN,      // Green - user marked as taken
        SKIPPED     // Red X - user marked as skipped
    }

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
        
        // Determine status
        val status = getMedicationStatus(drug)
        
        // Set icon and color based on status
        when (status) {
            MedicationStatus.UPCOMING -> {
                holder.statusIcon.setImageResource(R.drawable.ic_upcoming)
                holder.statusIcon.setColorFilter(
                    ContextCompat.getColor(holder.itemView.context, R.color.secondary_text)
                )
            }
            MedicationStatus.PENDING -> {
                holder.statusIcon.setImageResource(R.drawable.ic_pending)
                holder.statusIcon.setColorFilter(
                    ContextCompat.getColor(holder.itemView.context, R.color.warning_color)
                )
            }
            MedicationStatus.MISSED -> {
                holder.statusIcon.setImageResource(R.drawable.ic_missed)
                holder.statusIcon.setColorFilter(
                    ContextCompat.getColor(holder.itemView.context, R.color.error_color)
                )
            }
            MedicationStatus.TAKEN -> {
                holder.statusIcon.setImageResource(R.drawable.ic_check)
                holder.statusIcon.setColorFilter(
                    ContextCompat.getColor(holder.itemView.context, R.color.success_color)
                )
            }
            MedicationStatus.SKIPPED -> {
                holder.statusIcon.setImageResource(R.drawable.ic_close)
                holder.statusIcon.setColorFilter(
                    ContextCompat.getColor(holder.itemView.context, R.color.error_color)
                )
            }
        }
        
        // Click listener
        holder.itemView.setOnClickListener {
            onItemClick(drug)
        }
    }

    override fun getItemCount() = prescriptions.size

    /**
     * Determine the medication status based on time and user action
     */
    private fun getMedicationStatus(drug: Drug): MedicationStatus {
        // Check if user already marked status for today
        if (drug.todayStatus != null) {
            val today = getCurrentDate()
            if (drug.lastTakenDate == today) {
                return if (drug.todayStatus) MedicationStatus.TAKEN else MedicationStatus.SKIPPED
            }
        }
        
        // Calculate time-based status
        val minutesSinceScheduled = getMinutesSinceScheduledTime(drug.time)
        
        return when {
            minutesSinceScheduled < 0 -> MedicationStatus.UPCOMING    // Time hasn't come
            minutesSinceScheduled <= 5 -> MedicationStatus.PENDING     // 0-5 minutes
            else -> MedicationStatus.MISSED                             // > 5 minutes
        }
    }

    /**
     * Calculate minutes since scheduled time (negative if in future)
     */
    private fun getMinutesSinceScheduledTime(scheduledTime: String): Long {
        return try {
            val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
            val scheduled = sdf.parse(scheduledTime)
            
            val now = Calendar.getInstance()
            val scheduledCal = Calendar.getInstance()
            scheduledCal.time = scheduled ?: return -1
            
            // Set the same date for fair comparison
            scheduledCal.set(Calendar.YEAR, now.get(Calendar.YEAR))
            scheduledCal.set(Calendar.DAY_OF_YEAR, now.get(Calendar.DAY_OF_YEAR))
            
            // Calculate difference in minutes
            val diffMillis = now.timeInMillis - scheduledCal.timeInMillis
            diffMillis / (60 * 1000) // Convert to minutes
        } catch (e: Exception) {
            -1
        }
    }
    
    /**
     * Get current date in YYYY-MM-DD format
     */
    private fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }
}
