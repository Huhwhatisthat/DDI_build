package com.example.drugidentifier.data

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Singleton repository to store drugs across activities with persistence
 * Uses SharedPreferences to save data permanently
 */
object DrugRepository {
    private const val PREF_NAME = "DrugIdentifierPrefs"
    private const val KEY_DRUGS = "saved_drugs"
    
    // Store pairs of (Nickname, Ingredient)
    private val drugs = mutableSetOf<Pair<String, String>>()
    private lateinit var sharedPreferences: SharedPreferences
    private val gson = Gson()
    private var isInitialized = false
    
    /**
     * Initialize the repository with context
     * Must be called before using any other methods
     */
    fun init(context: Context) {
        if (!isInitialized) {
            sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            loadDrugsFromStorage()
            isInitialized = true
        }
    }
    
    /**
     * Load drugs from SharedPreferences
     */
    private fun loadDrugsFromStorage() {
        val drugsJson = sharedPreferences.getString(KEY_DRUGS, null)
        if (drugsJson != null) {
            val type = object : TypeToken<Set<Pair<String, String>>>() {}.type
            val loadedDrugs: Set<Pair<String, String>> = gson.fromJson(drugsJson, type)
            drugs.clear()
            drugs.addAll(loadedDrugs)
        }
    }
    
    /**
     * Save drugs to SharedPreferences
     */
    private fun saveDrugsToStorage() {
        val drugsJson = gson.toJson(drugs)
        sharedPreferences.edit().putString(KEY_DRUGS, drugsJson).apply()
    }
    
    fun addDrug(nickname: String, ingredient: String) {
        drugs.add(nickname to ingredient)
        saveDrugsToStorage()
    }
    
    fun removeDrug(nickname: String, ingredient: String) {
        drugs.remove(nickname to ingredient)
        saveDrugsToStorage()
    }
    
    fun getAllDrugs(): Set<Pair<String, String>> {
        return drugs.toSet()
    }
    
    fun getAllIngredients(): List<String> {
        return drugs.map { it.second }
    }
    
    fun clearAll() {
        drugs.clear()
        saveDrugsToStorage()
    }
    
    fun isEmpty(): Boolean {
        return drugs.isEmpty()
    }
    
    fun size(): Int {
        return drugs.size
    }
}
