package com.example.drugidentifier.data

/**
 * Singleton repository to store drugs across activities
 * This allows data to persist during the app session
 */
object DrugRepository {
    // Store pairs of (Nickname, Ingredient)
    private val drugs = mutableSetOf<Pair<String, String>>()
    
    fun addDrug(nickname: String, ingredient: String) {
        drugs.add(nickname to ingredient)
    }
    
    fun removeDrug(nickname: String, ingredient: String) {
        drugs.remove(nickname to ingredient)
    }
    
    fun getAllDrugs(): Set<Pair<String, String>> {
        return drugs.toSet()
    }
    
    fun getAllIngredients(): List<String> {
        return drugs.map { it.second }
    }
    
    fun clearAll() {
        drugs.clear()
    }
    
    fun isEmpty(): Boolean {
        return drugs.isEmpty()
    }
    
    fun size(): Int {
        return drugs.size
    }
}
