package com.codetech.api.dictionary

/**
 * Interface for listening to dictionary events.
 */
interface DictionaryListener {

    /**
     * Called when the dictionary process starts or stops.
     *
     * @param isStarted True if the dictionary process has started, false if it has stopped.
     */
    fun onProgress(isStarted: Boolean)

    /**
     * Called when the dictionary is successfully completed.
     *
     * @param wordInfo The dictionary text. Can be null if the dictionary fails or is not available.
     */
    fun onSuccess(wordInfo: WordInfo)

    /**
     * Called when the dictionary process fails.
     *
     * @param error The error message describing why the dictionary failed. Can be null if no specific error is provided.
     */
    fun onFailed(error: String?)
}