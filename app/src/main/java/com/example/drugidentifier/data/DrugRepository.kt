package com.example.drugidentifier.data

import android.content.Context
import android.content.SharedPreferences
import com.example.drugidentifier.models.Drug
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.*

object DrugRepository {
    private const val PREF_NAME = "DrugIdentifierPrefs"
    private const val KEY_DRUGS = "saved_drugs"
    
    private val drugs = mutableListOf<Drug>()
    private lateinit var sharedPreferences: SharedPreferences
    private val gson = Gson()
    private var isInitialized = false
    
    fun init(context: Context) {
        if (!isInitialized) {
            sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            loadDrugsFromStorage()
            isInitialized = true
        }
    }
    
    private fun loadDrugsFromStorage() {
        val drugsJson = sharedPreferences.getString(KEY_DRUGS, null)
        if (drugsJson != null) {
            try {
                val type = object : TypeToken<List<Drug>>() {}.type
                val loadedDrugs: List<Drug> = gson.fromJson(drugsJson, type)
                drugs.clear()
                drugs.addAll(loadedDrugs)
            } catch (e: Exception) {
                try {
                    val oldType = object : TypeToken<Set<Pair<String, String>>>() {}.type
                    val oldDrugs: Set<Pair<String, String>> = gson.fromJson(drugsJson, oldType)
                    drugs.clear()
                    drugs.addAll(oldDrugs.map { Drug(it.first, it.second) })
                    saveDrugsToStorage()
                } catch (migrationError: Exception) {
                    drugs.clear()
                }
            }
        }
    }
    
    private fun saveDrugsToStorage() {
        val drugsJson = gson.toJson(drugs)
        sharedPreferences.edit().putString(KEY_DRUGS, drugsJson).apply()
    }
    
    fun addDrug(drug: Drug) {
        drugs.add(drug)
        saveDrugsToStorage()
    }
    
    fun addDrug(nickname: String, ingredient: String) {
        drugs.add(Drug(nickname, ingredient))
        saveDrugsToStorage()
    }
    
    fun removeDrug(nickname: String, ingredient: String) {
        drugs.removeAll { it.name == nickname && it.activeIngredient == ingredient }
        saveDrugsToStorage()
    }
    
    fun getDrugsList(): List<Drug> {
        return drugs.toList()
    }
    
    fun getTodaysPrescriptions(): List<Drug> {
        return drugs.filter { it.time.isNotEmpty() }
            .sortedBy { parseTime(it.time) }
    }
    
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
    
    private fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }
    
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
    
    fun deleteDrug(nickname: String) {
        drugs.removeAll { it.name == nickname }
        saveDrugsToStorage()
    }
    
    fun getAllIngredients(): List<String> {
        return drugs.map { it.activeIngredient }
    }
    
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
