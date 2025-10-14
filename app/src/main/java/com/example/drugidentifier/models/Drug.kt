package com.example.drugidentifier.models

/**
 * Data class representing a medication
 * @param name The nickname/brand name of the drug
 * @param activeIngredient The active ingredient of the drug
 * @param quantity Number of pills/doses per time
 * @param frequency How often to take (e.g., "Every day", "Every month", "Twice a day")
 * @param time Time to take the medication (e.g., "10:30 AM", "04:00 PM")
 * @param lastTakenDate Date when medication was last marked as taken (YYYY-MM-DD format)
 * @param todayStatus Status for today: null (not set), true (taken), false (skipped)
 */
data class Drug(
    val name: String,
    val activeIngredient: String,
    val quantity: Int = 1,
    val frequency: String = "Every day",
    val time: String = "",
    val lastTakenDate: String = "",
    val todayStatus: Boolean? = null
)
