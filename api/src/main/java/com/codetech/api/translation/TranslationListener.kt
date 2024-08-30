package com.codetech.api.translation



/**
 * Interface for listening to translation events.
 */
interface TranslationListener {

    /**
     * Called when the translation process starts or stops.
     *
     * @param isStarted True if the translation process has started, false if it has stopped.
     */
    fun onProgress(isStarted: Boolean)

    /**
     * Called when the translation is successfully completed.
     *
     * @param translation The translated text. Can be null if the translation fails or is not available.
     */
    fun onSuccess(translation: String?)

    /**
     * Called when the translation process fails.
     *
     * @param error The error message describing why the translation failed. Can be null if no specific error is provided.
     */
    fun onFailed(error: String?)
}