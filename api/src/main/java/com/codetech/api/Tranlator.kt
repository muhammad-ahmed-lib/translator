package com.codetech.api

import android.content.Context
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.codetech.api.dictionary.DictionaryListener
import com.codetech.api.dictionary.WordInfo
import com.codetech.api.error.CustomExceptions.Companion.INVALID_TRANSLATION
import com.codetech.api.error.CustomExceptions.Companion.INVALID_WORD
import com.codetech.api.translation.TranslationListener
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

/**
 * Translator class for handling translation tasks using WorkManager.
 *
 * @param context The context of the application.
 * @param text The text to be translated.
 * @param sourceLanguage The source language code (e.g., "en" for English).
 * @param targetLanguage The target language code (e.g., "ar" for Arabic).
 * @constructor Creates a Translator instance.
 * @author Muhammad Ahmed
 * @date 08/28/2024
 */
class Translator {
    // Keys for input and output data in WorkManager
    private val KEY_INPUT_TEXT = "input_text"
    private val KEY_SOURCE_LANGUAGE = "source_language"
    private val KEY_TARGET_LANGUAGE = "target_language"
    private val KEY_RESULT_TEXT = "result_text"
    private val KEY_IS_LOADING = "is_loading"
    private val KEY_ERROR_MESSAGE = "error_message"
    private var context: Context
    private var text: String? = null
    private var sourceLanguage: String? = null
    private var targetLanguage: String? = null
    private var word: String? = null
    private val TAG = "TranslatorInfo"

    public constructor(
        context: Context,
        text: String,
        sourceLanguage: String,
        targetLanguage: String
    ) {
        this.context = context
        this.text = text
        this.sourceLanguage = sourceLanguage
        this.targetLanguage = targetLanguage
    }

    public constructor(
        context: Context,
        word: String,
        ) {
        this.context = context
        this.word = word
    }

    // Translation listener
    private var mTranslationListener: TranslationListener? = null

    /**
     * Starts the translation process and uses a listener to report progress and results.
     *
     * @param listener The TranslationListener to report progress and results.
     */
    fun translate(listener: TranslationListener) {
        mTranslationListener = listener
        if (text != null && sourceLanguage != null && targetLanguage != null) {
            val inputData = Data.Builder()
                .putString(KEY_INPUT_TEXT, text)
                .putString(KEY_SOURCE_LANGUAGE, sourceLanguage)
                .putString(KEY_TARGET_LANGUAGE, targetLanguage)
                .build()

            val translationWorkRequest = OneTimeWorkRequestBuilder<TranslationWorker>()
                .setInputData(inputData)
                .build()

            WorkManager.getInstance(context).enqueue(translationWorkRequest)

            WorkManager.getInstance(context).getWorkInfoByIdLiveData(translationWorkRequest.id)
                .observe(context as LifecycleOwner) { workInfo ->
                    workInfo?.let {
                        val isLoading = workInfo.progress.getBoolean(KEY_IS_LOADING, false)
                        mTranslationListener?.onProgress(isLoading)

                        if (workInfo.state.isFinished) {
                            if (workInfo.state == WorkInfo.State.SUCCEEDED) {
                                val translatedText = workInfo.outputData.getString(KEY_RESULT_TEXT)
                                mTranslationListener?.onSuccess(translatedText)
                            } else if (workInfo.state == WorkInfo.State.FAILED) {
                                val errorMessage = workInfo.outputData.getString(KEY_ERROR_MESSAGE)
                                mTranslationListener?.onFailed(errorMessage)
                            }
                        }
                    }
                }
        } else {
            mTranslationListener?.onFailed(INVALID_TRANSLATION)
        }
    }

    /**
     * Starts the translation process and uses callbacks to report progress and results.
     *
     * @param onProgress Callback for reporting loading state.
     * @param onSuccess Callback for reporting the translated text.
     * @param onFailed Callback for reporting any errors.
     */
    fun translate(
        onProgress: (Boolean) -> Unit,
        onSuccess: (String?) -> Unit,
        onFailed: (String?) -> Unit
    ) {
        if (text != null && sourceLanguage != null && targetLanguage != null) {
            val inputData = Data.Builder()
                .putString(KEY_INPUT_TEXT, text)
                .putString(KEY_SOURCE_LANGUAGE, sourceLanguage)
                .putString(KEY_TARGET_LANGUAGE, targetLanguage)
                .build()

            val translationWorkRequest = OneTimeWorkRequestBuilder<TranslationWorker>()
                .setInputData(inputData)
                .build()

            WorkManager.getInstance(context).enqueue(translationWorkRequest)

            WorkManager.getInstance(context).getWorkInfoByIdLiveData(translationWorkRequest.id)
                .observe(context as LifecycleOwner) { workInfo ->
                    workInfo?.let {
                        val isLoading = workInfo.progress.getBoolean(KEY_IS_LOADING, false)
                        onProgress(isLoading)

                        if (workInfo.state.isFinished) {
                            if (workInfo.state == WorkInfo.State.SUCCEEDED) {
                                val translatedText = workInfo.outputData.getString(KEY_RESULT_TEXT)
                                onSuccess(translatedText)
                            } else if (workInfo.state == WorkInfo.State.FAILED) {
                                val errorMessage = workInfo.outputData.getString(KEY_ERROR_MESSAGE)
                                onFailed(errorMessage)
                            }
                        }
                    }
                }
        } else {
            onFailed(INVALID_TRANSLATION)
        }

    }

    /**
     * Starts the dictionary process and uses a listener to report progress and results.
     *
     * @param listener The DictionaryListener to report progress and results.
     */
    private var mDictionaryListener: DictionaryListener? = null

    fun wordInfo(listener: DictionaryListener) {
        mDictionaryListener = listener
        if (word != null) {
            mDictionaryListener?.onProgress(true)
            val client = OkHttpClient()
            CoroutineScope(Dispatchers.IO).launch {
                val request = Request.Builder()
                    .url("https://api.dictionaryapi.dev/api/v2/entries/en/$word")
                    .build()
                try {
                    val response = client.newCall(request).execute()
                    if (response.isSuccessful) {
                        val responseBody = response.body.string()
                        val gson = Gson()
                        val wordResponse: List<WordInfo> = gson.fromJson(responseBody, Array<WordInfo>::class.java).toList()
                        withContext(Dispatchers.Main) {
                            mDictionaryListener?.onProgress(false)
                            // Update UI with the word response data
                            mDictionaryListener?.onSuccess(wordResponse[0])
                        }
                    } else {
                        withContext(Dispatchers.Main){
                            mDictionaryListener?.onFailed(response.message)
                            mDictionaryListener?.onProgress(false)
                        }
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    withContext(Dispatchers.Main){
                        mDictionaryListener?.onFailed(e.message)
                        mDictionaryListener?.onProgress(false)
                    }

                }
            }
        } else {
            mDictionaryListener?.onFailed(INVALID_TRANSLATION)
        }
    }

    /**
     * Starts the translation process and uses callbacks to report progress and results.
     *
     * @param onProgress Callback for reporting loading state.
     * @param onSuccess Callback for reporting the translated text.
     * @param onFailed Callback for reporting any errors.
     */
    fun wordInfo(
        onProgress: (Boolean) -> Unit,
        onSuccess: (WordInfo?) -> Unit,
        onFailed: (String?) -> Unit
    ) {
        if (word != null) {
            onProgress(true)
            val client = OkHttpClient()
            CoroutineScope(Dispatchers.IO).launch {
                val request = Request.Builder()
                    .url("https://api.dictionaryapi.dev/api/v2/entries/en/$word")
                    .build()

                try {
                    val response = client.newCall(request).execute()
                    if (response.isSuccessful) {
                        val responseBody = response.body.string()
                        val gson = Gson()
                        val wordResponse: List<WordInfo> = gson.fromJson(responseBody, Array<WordInfo>::class.java).toList()
                        withContext(Dispatchers.Main) {
                            onProgress(false)
                            // Update UI with the word response data
                            onSuccess(wordResponse[0])
                        }
                    } else {
                       withContext(Dispatchers.Main){
                           onProgress(false)
                           onFailed(response.message)
                       }
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    withContext(Dispatchers.Main){
                        onProgress(false)
                        onFailed(e.message)
                    }
                }
            }
        } else {
            onFailed(INVALID_WORD)
        }

    }
}
