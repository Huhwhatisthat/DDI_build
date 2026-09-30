package com.example.drugidentifier

import com.example.drugidentifier.models.DrugInteraction
import com.example.drugidentifier.models.DrugInteraction.Severity

object InteractionChecker {

    private val knownInteractions = mapOf(
        "ibuprofen|naproxen" to DrugInteraction(
            drug1 = "Ibuprofen",
            drug2 = "Naproxen",
            severity = Severity.HIGH,
            description = "Increased risk of severe gastrointestinal bleeding and kidney damage"
        ),
        
        "antacid|iron" to DrugInteraction(
            drug1 = "Antacid",
            drug2 = "Iron",
            severity = Severity.MODERATE,
            description = "Reduced iron absorption, leading to treatment failure for anemia"
        ),
        "antacid|ferrous sulfate" to DrugInteraction(
            drug1 = "Antacid",
            drug2 = "Ferrous Sulfate",
            severity = Severity.MODERATE,
            description = "Reduced iron absorption, leading to treatment failure for anemia"
        ),
        
        "antacid|bisacodyl" to DrugInteraction(
            drug1 = "Bisacodyl",
            drug2 = "Antacid",
            severity = Severity.MODERATE,
            description = "Premature dissolution of tablet coating, causing stomach irritation"
        ),
        
        "chlorpheniramine|diphenhydramine" to DrugInteraction(
            drug1 = "Diphenhydramine",
            drug2 = "Chlorpheniramine",
            severity = Severity.MODERATE,
            description = "Severe drowsiness, impaired coordination, increased risk of accidents"
        ),
        
        "caffeine|pseudoephedrine" to DrugInteraction(
            drug1 = "Pseudoephedrine",
            drug2 = "Caffeine",
            severity = Severity.MODERATE,
            description = "Increased heart rate, blood pressure, anxiety, and insomnia"
        ),
        
        "cimetidine|loperamide" to DrugInteraction(
            drug1 = "Loperamide",
            drug2 = "Cimetidine",
            severity = Severity.HIGH,
            description = "Increased loperamide levels, potential for serious cardiac side effects"
        ),
        
        "dextromethorphan|diphenhydramine" to DrugInteraction(
            drug1 = "Dextromethorphan",
            drug2 = "Diphenhydramine",
            severity = Severity.MODERATE,
            description = "Additive drowsiness, dizziness, and impaired cognitive function"
        ),
        "chlorpheniramine|dextromethorphan" to DrugInteraction(
            drug1 = "Dextromethorphan",
            drug2 = "Chlorpheniramine",
            severity = Severity.MODERATE,
            description = "Additive drowsiness, dizziness, and impaired cognitive function"
        ),
        
        "iron|omeprazole" to DrugInteraction(
            drug1 = "Omeprazole",
            drug2 = "Iron",
            severity = Severity.MODERATE,
            description = "Reduced iron absorption due to decreased stomach acidity"
        ),
        "ferrous sulfate|omeprazole" to DrugInteraction(
            drug1 = "Omeprazole",
            drug2 = "Ferrous Sulfate",
            severity = Severity.MODERATE,
            description = "Reduced iron absorption due to decreased stomach acidity"
        ),
        "famotidine|iron" to DrugInteraction(
            drug1 = "Famotidine",
            drug2 = "Iron",
            severity = Severity.MODERATE,
            description = "Reduced iron absorption due to decreased stomach acidity"
        ),
        "famotidine|ferrous sulfate" to DrugInteraction(
            drug1 = "Famotidine",
            drug2 = "Ferrous Sulfate",
            severity = Severity.MODERATE,
            description = "Reduced iron absorption due to decreased stomach acidity"
        ),
        
        "bisacodyl|lactulose" to DrugInteraction(
            drug1 = "Bisacodyl",
            drug2 = "Lactulose",
            severity = Severity.MODERATE,
            description = "Increased risk of dehydration, electrolyte imbalance, and severe cramping"
        ),
        "lactulose|senna" to DrugInteraction(
            drug1 = "Senna",
            drug2 = "Lactulose",
            severity = Severity.MODERATE,
            description = "Increased risk of dehydration, electrolyte imbalance, and severe cramping"
        ),
        
        "antacid|aspirin" to DrugInteraction(
            drug1 = "Antacid",
            drug2 = "Aspirin",
            severity = Severity.LOW,
            description = "Decreased absorption and effectiveness of aspirin for pain relief"
        ),
        "acetylsalicylic acid|antacid" to DrugInteraction(
            drug1 = "Antacid",
            drug2 = "Acetylsalicylic Acid",
            severity = Severity.LOW,
            description = "Decreased absorption and effectiveness of aspirin for pain relief"
        )
    )

    fun checkInteractions(ingredients: List<String>): List<DrugInteraction> {
        val interactions = mutableSetOf<DrugInteraction>()
        
        val normalizedIngredients = ingredients.map { it.lowercase().trim() }
        
        val uniqueIngredients = normalizedIngredients.distinct()
        
        for (i in uniqueIngredients.indices) {
            for (j in i + 1 until uniqueIngredients.size) {
                val ingredient1 = uniqueIngredients[i]
                val ingredient2 = uniqueIngredients[j]
                
                if (ingredient1 == ingredient2) continue
                
                val key = if (ingredient1 < ingredient2) {
                    "$ingredient1|$ingredient2"
                } else {
                    "$ingredient2|$ingredient1"
                }
                
                knownInteractions[key]?.let { interaction ->
                    interactions.add(interaction)
                }
            }
        }
        
        return interactions.toList()
    }

    fun getSeverityColor(severity: Severity): Int {
        return when (severity) {
            Severity.HIGH -> R.color.error
            Severity.MODERATE -> R.color.warning
            Severity.LOW -> R.color.info
        }
    }

    fun getSeverityText(severity: Severity): String {
        return when (severity) {
            Severity.HIGH -> "HIGH"
            Severity.MODERATE -> "MODERATE"
            Severity.LOW -> "MINOR"
        }
    }
}
