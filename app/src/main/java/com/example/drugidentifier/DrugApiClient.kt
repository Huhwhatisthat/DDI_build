package com.example.drugidentifier

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.ResponseBody // Import ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject // Import Android's built-in JSON parser
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

// --- Data Classes (We only need the first one now) ---
data class RxNormResponse(val approximateGroup: ApproximateGroup)
data class ApproximateGroup(val candidate: List<Candidate>?)
data class Candidate(val rxcui: String)

// --- Retrofit API Service Interface (Simplified) ---
interface NihApiService {
    @GET("approximateTerm.json")
    suspend fun getRxcui(@Query("term") drugName: String): RxNormResponse

    // This now returns a raw ResponseBody instead of our custom class
    @GET("interaction/list.json")
    suspend fun getInteractionList(
        @Query("rxcuis", encoded = true) rxcuis: String
    ): ResponseBody // Return type changed to ResponseBody
}

// --- The Main Client Object ---
object DrugApiClient {

    private val apiService: NihApiService by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val httpClient = OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor { chain ->
                val original = chain.request()
                val requestBuilder = original.newBuilder()
                    .header("User-Agent", "Mozilla/5.0 (Android 11; Mobile; rv:68.0) Gecko/68.0 Firefox/68.0")
                val request = requestBuilder.build()
                chain.proceed(request)
            }
            .build()

        Retrofit.Builder()
            .baseUrl("https://rxnav.nlm.nih.gov/REST/")
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create()) // Gson converter is still fine
            .build()
            .create(NihApiService::class.java)
    }

    suspend fun checkInteractions(ingredients: List<String>): String? {
        if (ingredients.size < 2) return null

        val rxcuis = ingredients.mapNotNull { ingredient ->
            try {
                apiService.getRxcui(ingredient).approximateGroup.candidate?.firstOrNull()?.rxcui
            } catch (e: Exception) {
                null
            }
        }

        if (rxcuis.size < ingredients.size || rxcuis.size < 2) {
            return "Could not find all drugs in the database."
        }

        return try {
            val sortedRxcuiString = rxcuis.map { it.toInt() }.sorted().joinToString("+")

            // 1. Get the raw response body
            val responseBody = apiService.getInteractionList(sortedRxcuiString)

            // 2. Convert it to a String
            val jsonString = responseBody.string()
            Log.d("DrugApiClient", "Raw JSON Response: $jsonString")

            // 3. Manually parse the String to find the description
            val jsonObject = JSONObject(jsonString)
            val description = jsonObject.optJSONArray("fullInteractionTypeGroup")
                ?.optJSONObject(0)
                ?.optJSONArray("fullInteractionType")
                ?.optJSONObject(0)
                ?.optJSONArray("interactionPair")
                ?.optJSONObject(0)
                ?.optString("description")

            if (!description.isNullOrEmpty()) {
                Log.d("DrugApiClient", "SUCCESS: Manually parsed interaction: $description")
            } else {
                Log.d("DrugApiClient", "SUCCESS: No interaction description found in JSON.")
            }
            description

        } catch (e: Exception) {
            Log.e("DrugApiClient", "Error during manual parsing: ${e.message}", e)
            "Error: Could not retrieve or parse interaction data."
        }
    }
}