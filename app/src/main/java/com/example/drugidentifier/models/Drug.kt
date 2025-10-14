package com.example.drugidentifier.models

/**
 * Data class representing a medication
 * @param name The nickname/brand name of the drug
 * @param activeIngredient The active ingredient of the drug
 */
data class Drug(
    val name: String,
    val activeIngredient: String
)
