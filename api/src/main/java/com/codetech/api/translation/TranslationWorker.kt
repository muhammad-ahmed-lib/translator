package com.codetech.api

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONException
import java.io.IOException
import java.net.URLEncoder

/**
 * Worker class for handling translation tasks using Google Translate API.
 *
 * @param context The context of the application.
 * @param params Worker parameters including input data and constraints.
 */
class TranslationWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    // Keys for input and output data in WorkManager
    private val KEY_INPUT_TEXT = "input_text"
    private val KEY_SOURCE_LANGUAGE = "source_language"
    private val KEY_TARGET_LANGUAGE = "target_language"
    private val KEY_RESULT_TEXT = "result_text"
    private val KEY_IS_LOADING = "is_loading"
    private val KEY_ERROR_MESSAGE = "error_message"

    /**
     * Performs the translation work in a coroutine context.
     *
     * @return Result indicating success or failure of the work.
     */
    override suspend fun doWork(): Result {
        // Set progress to indicate loading has started
        setProgressAsync(workDataOf(KEY_IS_LOADING to true))

        // Retrieve input data
        val text = inputData.getString(KEY_INPUT_TEXT) ?: return Result.failure()
        val sourceLanguage = inputData.getString(KEY_SOURCE_LANGUAGE) ?: "en"
        val targetLanguage = inputData.getString(KEY_TARGET_LANGUAGE) ?: "ur"

        // Encode the text for URL
        val encodedText = withContext(Dispatchers.IO) {
            URLEncoder.encode(text, "utf-8")
        }

        // Build the translation request URL
        val url = "https://translate.googleapis.com/translate_a/single?client=gtx&sl=$sourceLanguage&tl=$targetLanguage&dt=t&q=$encodedText"

        // Create OkHttpClient and request
        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()

        return try {
            // Execute the request and get the response
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val responseBody = response.body.string()
                val translatedText = extractTranslatedText(responseBody)
                // Set progress to indicate loading has finished
                setProgressAsync(workDataOf(KEY_IS_LOADING to false))
                // Return success with translated text
                val outputData = workDataOf(KEY_RESULT_TEXT to translatedText)
                Result.success(outputData)
            } else {
                // Set progress to indicate loading has finished
                setProgressAsync(workDataOf(KEY_IS_LOADING to false))
                // Return failure with error message
                val errorData = workDataOf(KEY_ERROR_MESSAGE to "Failed with response code: ${response.code}")
                Result.failure(errorData)
            }
        } catch (e: IOException) {
            // Set progress to indicate loading has finished
            setProgressAsync(workDataOf(KEY_IS_LOADING to false))
            // Return failure with exception message
            val errorData = workDataOf(KEY_ERROR_MESSAGE to e.message)
            Result.failure(errorData)
        }
    }

    /**
     * Extracts the translated text from the response body.
     *
     * @param responseBody The JSON response body from the translation API.
     * @return The translated text or an error message if parsing fails.
     */
    private fun extractTranslatedText(responseBody: String?): String {
        return try {
            val jsonArray = JSONArray(responseBody)
            val translatedTextArray = jsonArray.getJSONArray(0).getJSONArray(0)
            translatedTextArray.getString(0)  // Get the translated text
        } catch (e: JSONException) {
            "Error parsing translation"  // Return error message if JSON parsing fails
        }
    }
}
