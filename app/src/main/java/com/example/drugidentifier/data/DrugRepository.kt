package com.example.drugidentifier.data

import android.content.Context
import android.content.SharedPreferences
import com.example.drugidentifier.models.Drug
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.*

/**
 * Singleton repository to store drugs across activities with persistence
 * Uses SharedPreferences to save data permanently
 */
object DrugRepository {
    private const val PREF_NAME = "DrugIdentifierPrefs"
    private const val KEY_DRUGS = "saved_drugs"
    
    // Store Drug objects with full prescription information
    private val drugs = mutableListOf<Drug>()
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
            try {
                val type = object : TypeToken<List<Drug>>() {}.type
                val loadedDrugs: List<Drug> = gson.fromJson(drugsJson, type)
                drugs.clear()
                drugs.addAll(loadedDrugs)
            } catch (e: Exception) {
                // Migration: Try loading old format (Pair<String, String>)
                try {
                    val oldType = object : TypeToken<Set<Pair<String, String>>>() {}.type
                    val oldDrugs: Set<Pair<String, String>> = gson.fromJson(drugsJson, oldType)
                    drugs.clear()
                    drugs.addAll(oldDrugs.map { Drug(it.first, it.second) })
                    saveDrugsToStorage() // Save in new format
                } catch (migrationError: Exception) {
                    drugs.clear()
                }
            }
        }
    }
    
    /**
     * Save drugs to SharedPreferences
     */
    private fun saveDrugsToStorage() {
        val drugsJson = gson.toJson(drugs)
        sharedPreferences.edit().putString(KEY_DRUGS, drugsJson).apply()
    }
    
    /**
     * Add a drug with full prescription information
     */
    fun addDrug(drug: Drug) {
        drugs.add(drug)
        saveDrugsToStorage()
    }
    
    /**
     * Legacy method for backward compatibility
     */
    fun addDrug(nickname: String, ingredient: String) {
        drugs.add(Drug(nickname, ingredient))
        saveDrugsToStorage()
    }
    
    /**
     * Remove a drug
     */
    fun removeDrug(nickname: String, ingredient: String) {
        drugs.removeAll { it.name == nickname && it.activeIngredient == ingredient }
        saveDrugsToStorage()
    }
    
    /**
     * Get all drugs as a list of Drug objects
     */
    fun getDrugsList(): List<Drug> {
        return drugs.toList()
    }
    
    /**
     * Get today's prescriptions (drugs scheduled for today)
     */
    fun getTodaysPrescriptions(): List<Drug> {
        return drugs.filter { it.time.isNotEmpty() }
            .sortedBy { parseTime(it.time) }
    }
    
    /**
     * Update medication status for today (taken or skipped)
     */
    fun updateMedicationStatus(drugName: String, taken: Boolean) {
        val today = getCurrentDate()
        val index = drugs.indexOfFirst { it.name == drugName }
        
        if (index != -1) {
            val drug = drugs[index]
            val updatedDrug = drug.copy(
                lastTakenDate = today,
                todayStatus = taken
            )
            drugs[index] = updatedDrug
            saveDrugsToStorage()
        }
    }
    
    /**
     * Get current date in YYYY-MM-DD format
     */
    private fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }
    
    /**
     * Parse time string to comparable format (24-hour)
     */
    private fun parseTime(timeStr: String): Int {
        return try {
            val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
            val date = sdf.parse(timeStr)
            val cal = Calendar.getInstance()
            cal.time = date ?: return 0
            cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE)
        } catch (e: Exception) {
            0
        }
    }
    
    /**
     * Delete a drug by its nickname
     */
    fun deleteDrug(nickname: String) {
        drugs.removeAll { it.name == nickname }
        saveDrugsToStorage()
    }
    
    /**
     * Get all active ingredients for interaction checking
     */
    fun getAllIngredients(): List<String> {
        return drugs.map { it.activeIngredient }
    }
    
    /**
     * Legacy method for backward compatibility
     */
    fun getAllDrugs(): Set<Pair<String, String>> {
        return drugs.map { it.name to it.activeIngredient }.toSet()
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
