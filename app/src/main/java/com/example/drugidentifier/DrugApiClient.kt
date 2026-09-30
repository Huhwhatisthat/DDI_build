package com.example.drugidentifier

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object DrugApiClient {

    private val interactionDatabase = mapOf(
        "ibuprofen" to mapOf(
            "aspirin" to "High risk of stomach bleeding. Ibuprofen can also reduce the heart-protective effects of low-dose aspirin.",
            "pseudoephedrine" to "Increases blood pressure and may cause a racing heart or anxiety. Use with caution, especially if you have a history of heart conditions or high blood pressure.",
            "naproxen" to "Increased risk of stomach bleeding. Both are NSAIDs and should not be taken together."
        ),
        "aspirin" to mapOf(
            "ibuprofen" to "High risk of stomach bleeding. Taking both can also reduce aspirin's heart-protective effects.",
            "naproxen" to "Increased risk of stomach bleeding."
        ),
        "pseudoephedrine" to mapOf(
            "ibuprofen" to "Increases blood pressure and may cause a racing heart or anxiety. Use with caution if you have a history of high blood pressure."
        ),
        "doxycycline" to mapOf(
            "calcium carbonate" to "Antacids containing calcium prevent the body from absorbing doxycycline, leading to treatment failure. Take at least 2 hours before or 4 hours after antacids."
        ),
        "calcium carbonate" to mapOf(
            "doxycycline" to "Prevents the body from absorbing doxycycline, leading to treatment failure. Take at least 2 hours before or 4 hours after."
        ),
        "naproxen" to mapOf(
            "ibuprofen" to "Increased risk of stomach bleeding. Both are NSAIDs and should not be taken together.",
            "aspirin" to "Increased risk of stomach bleeding."
        ),
        "diphenhydramine" to mapOf(
            "cetirizine" to "Increased drowsiness and sedation. Avoid operating machinery."
        ),
        "cetirizine" to mapOf(
            "diphenhydramine" to "Increased drowsiness and sedation. Avoid operating machinery."
        )
    )

    suspend fun checkInteractions(ingredients: List<String>): String? {
        return withContext(Dispatchers.Default) {
            if (ingredients.size < 2) return@withContext null

            Log.d("DrugApiClient", "Checking interactions locally for: $ingredients")

            for (i in ingredients.indices) {
                for (j in i + 1 until ingredients.size) {
                    val drug1 = ingredients[i].lowercase()
                    val drug2 = ingredients[j].lowercase()

                    interactionDatabase[drug1]?.get(drug2)?.let {
                        val message = "Interaction Found: $it"
                        Log.d("DrugApiClient", message)
                        return@withContext message
                    }
                    interactionDatabase[drug2]?.get(drug1)?.let {
                        val message = "Interaction Found: $it"
                        Log.d("DrugApiClient", message)
                        return@withContext message
                    }
                }
            }

            Log.d("DrugApiClient", "No interactions found in local database.")
            "No interactions found."
        }
    }
}