package com.example.drugidentifier

import android.util.Log
import okhttp3.OkHttpClient // New import
import okhttp3.logging.HttpLoggingInterceptor // New import
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

// --- Data Classes (No changes here) ---
// ... (Your data classes remain the same) ...
data class RxNormResponse(val approximateGroup: ApproximateGroup)
data class ApproximateGroup(val candidate: List<Candidate>?)
data class Candidate(val rxcui: String)

data class InteractionResponse(val fullInteractionTypeGroup: List<FullInteractionTypeGroup>?)
data class FullInteractionTypeGroup(val fullInteractionType: List<FullInteractionType>?)
data class FullInteractionType(val interactionPair: List<InteractionPair>)
data class InteractionPair(val description: String)

// --- Retrofit API Service Interface (No changes here) ---
interface NihApiService {
    @GET("approximateTerm.json")
    suspend fun getRxcui(@Query("term") drugName: String): RxNormResponse

    @GET("interaction/list.json")
    suspend fun getInteractions(@Query("rxcuis", encoded = true) rxcuis: String): InteractionResponse
}


// --- The Main Client Object ---

object DrugApiClient {

    // ▼▼▼ THIS ENTIRE SECTION IS NEW ▼▼▼
    // We are creating a more advanced client that includes a logger.
    private val apiService: NihApiService by lazy {

        // 1. Create the Logging Interceptor
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY) // Log everything: URL, headers, body

        // 2. Create the OkHttp Client and add the interceptor
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        // 3. Build Retrofit and attach the custom OkHttp client
        Retrofit.Builder()
            .baseUrl("https://rxnav.nlm.nih.gov/REST/")
            .client(client) // Attach the client with the logger
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NihApiService::class.java)
    }
    // ▲▲▲ END OF NEW SECTION ▲▲▲


    // The checkInteractions function remains exactly the same.
    suspend fun checkInteractions(ingredients: List<String>): String? {
        // ... (The rest of your checkInteractions function is unchanged) ...
        if (ingredients.size < 2) return null
        Log.d("DrugApiClient", "Starting interaction check for: $ingredients")
        val rxcuis = ingredients.mapNotNull { ingredient ->
            try {
                Log.d("DrugApiClient", "Searching for RxCUI for: '$ingredient'")
                val response = apiService.getRxcui(ingredient)
                val rxcui = response.approximateGroup.candidate?.firstOrNull()?.rxcui
                if (rxcui != null) {
                    Log.d("DrugApiClient", "SUCCESS: Found RxCUI $rxcui for '$ingredient'")
                } else {
                    Log.e("DrugApiClient", "FAILURE: Could not find RxCUI for '$ingredient'. Response was empty.")
                }
                rxcui
            } catch (e: Exception) {
                Log.e("DrugApiClient", "FAILURE: Network error for '$ingredient': ${e.message}")
                null
            }
        }
        if (rxcuis.size < ingredients.size || rxcuis.size < 2) {
            Log.e("DrugApiClient", "Could not resolve all ingredients. Found ${rxcuis.size} of ${ingredients.size}.")
            return "Could not find all drugs in the database."
        }
        Log.d("DrugApiClient", "Checking interactions for RxCUIs: $rxcuis")
        return try {
            val response = apiService.getInteractions(rxcuis.joinToString("+"))
            val description = response.fullInteractionTypeGroup
                ?.firstOrNull()?.fullInteractionType
                ?.firstOrNull()?.interactionPair
                ?.firstOrNull()?.description
            if (description != null) {
                Log.d("DrugApiClient", "Interaction found: $description")
            } else {
                Log.d("DrugApiClient", "No interactions found in API response.")
            }
            description
        } catch (e: Exception) {
            Log.e("DrugApiClient", "Error during interaction check: ${e.message}")
            "Error checking for interactions. Please check your internet connection."
        }
    }
}