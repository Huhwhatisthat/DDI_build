package com.example.drugidentifier

import com.example.drugidentifier.models.DrugInteraction
import com.example.drugidentifier.models.DrugInteraction.Severity

/**
 * Utility to check for drug interactions based on active ingredients
 * Reference: INTERACTIONS-REFERENCE.md
 */
object InteractionChecker {

    // Map of ingredient pairs to their interactions
    // Key format: "ingredient1|ingredient2" (alphabetically sorted, lowercase)
    private val knownInteractions = mapOf(
        // 1. Ibuprofen + Naproxen (Both NSAIDs)
        "ibuprofen|naproxen" to DrugInteraction(
            drug1 = "Ibuprofen",
            drug2 = "Naproxen",
            severity = Severity.HIGH,
            description = "Increased risk of severe gastrointestinal bleeding and kidney damage"
        ),
        
        // 2. Antacids + Iron Supplements
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
        
        // 3. Bisacodyl + Antacids
        "antacid|bisacodyl" to DrugInteraction(
            drug1 = "Bisacodyl",
            drug2 = "Antacid",
            severity = Severity.MODERATE,
            description = "Premature dissolution of tablet coating, causing stomach irritation"
        ),
        
        // 4. Sedating Antihistamine + Sedating Antihistamine
        "chlorpheniramine|diphenhydramine" to DrugInteraction(
            drug1 = "Diphenhydramine",
            drug2 = "Chlorpheniramine",
            severity = Severity.MODERATE,
            description = "Severe drowsiness, impaired coordination, increased risk of accidents"
        ),
        
        // 5. Pseudoephedrine + Caffeine
        "caffeine|pseudoephedrine" to DrugInteraction(
            drug1 = "Pseudoephedrine",
            drug2 = "Caffeine",
            severity = Severity.MODERATE,
            description = "Increased heart rate, blood pressure, anxiety, and insomnia"
        ),
        
        // 6. Loperamide + Cimetidine
        "cimetidine|loperamide" to DrugInteraction(
            drug1 = "Loperamide",
            drug2 = "Cimetidine",
            severity = Severity.HIGH,
            description = "Increased loperamide levels, potential for serious cardiac side effects"
        ),
        
        // 7. Dextromethorphan + Sedating Antihistamines
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
        
        // 8. Acid Reducers (PPI/H2 Blockers) + Iron Supplements
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
        
        // 9. Stimulant Laxative + Osmotic Laxative
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
        
        // 10. Antacids + Aspirin
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

    /**
     * Check for interactions between a list of active ingredients
     * @param ingredients List of active ingredient names
     * @return List of detected interactions (without duplicates)
     */
    fun checkInteractions(ingredients: List<String>): List<DrugInteraction> {
        val interactions = mutableSetOf<DrugInteraction>() // Use Set to avoid duplicates
        
        // Normalize ingredients to lowercase and trim whitespace
        val normalizedIngredients = ingredients.map { it.lowercase().trim() }
        
        // Remove duplicates from input
        val uniqueIngredients = normalizedIngredients.distinct()
        
        // Check all pairs of ingredients
        for (i in uniqueIngredients.indices) {
            for (j in i + 1 until uniqueIngredients.size) {
                val ingredient1 = uniqueIngredients[i]
                val ingredient2 = uniqueIngredients[j]
                
                // Skip if both ingredients are the same
                if (ingredient1 == ingredient2) continue
                
                // Create key (alphabetically sorted)
                val key = if (ingredient1 < ingredient2) {
                    "$ingredient1|$ingredient2"
                } else {
                    "$ingredient2|$ingredient1"
                }
                
                // Check if interaction exists
                knownInteractions[key]?.let { interaction ->
                    interactions.add(interaction)
                }
            }
        }
        
        return interactions.toList()
    }

    /**
     * Get severity color resource ID
     */
    fun getSeverityColor(severity: Severity): Int {
        return when (severity) {
            Severity.HIGH -> R.color.error
            Severity.MODERATE -> R.color.warning
            Severity.LOW -> R.color.info
        }
    }

    /**
     * Get severity text
     */
    fun getSeverityText(severity: Severity): String {
        return when (severity) {
            Severity.HIGH -> "HIGH"
            Severity.MODERATE -> "MODERATE"
            Severity.LOW -> "MINOR"
        }
    }
}
