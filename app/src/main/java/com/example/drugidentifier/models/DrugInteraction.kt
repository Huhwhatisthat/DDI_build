package com.example.drugidentifier.models

/**
 * Data class representing a drug interaction
 */
data class DrugInteraction(
    val drug1: String,
    val drug2: String,
    val severity: Severity,
    val description: String
) {
    enum class Severity {
        HIGH,
        MODERATE,
        LOW
    }
}
