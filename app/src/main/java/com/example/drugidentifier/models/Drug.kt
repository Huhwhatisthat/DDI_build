package com.example.drugidentifier.models

data class Drug(
    val name: String,
    val activeIngredient: String,
    val quantity: Int = 1,
    val frequency: String = "Every day",
    val time: String = "",
    val lastTakenDate: String = "",
    val todayStatus: Boolean? = null
)
