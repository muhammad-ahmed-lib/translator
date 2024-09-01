Translator Library

The Translator Library provides functionality to translate text using the Google Translate API via WorkManager. It also offers word information (dictionary) features, allowing users to fetch detailed information about specific words. The library supports both callback-based and higher-order function approaches for handling translation and word info retrieval results.

Features

Translate Text: Use the Google Translate API to translate text between languages.
Fetch Word Info: Retrieve dictionary information about specific words, including definitions, examples, and more.
Flexible Integration: Handle loading, success, and failure states through callbacks or higher-order functions.
Background Processing: Utilizes WorkManager for background tasks to ensure smooth UI performance.

Sample video

https://github.com/user-attachments/assets/57aba4e7-05b7-4e8a-96d1-ce2597434b0c

Setup

Add Internet Permission

Ensure you have internet permission in your AndroidManifest.xml:

xml

Copy code

<uses-permission android:name="android.permission.INTERNET" />

Usage

1. Create a Translator Instance

Initialize the Translator with the context and the text or word you want to process. Specify source and target languages if you’re translating.

kotlin

Copy code

val translator = Translator(context, text, sourceLanguage, targetLanguage)

2. Translate Text with Callbacks

Use the translate function with TranslationListener callbacks:

kotlin

Copy code

translator.translate(object : TranslationListener {
    
    override fun onProgress(isStarted: Boolean) {
        // Handle loading state
    }

    override fun onSuccess(translation: String?) {
        // Handle translated text
    }

    override fun onFailed(error: String?) {
        // Handle failure
    }
})

3. Translate Text with Higher-Order Functions

Alternatively, use the translate function with higher-order function parameters:

kotlin

Copy code

translator.translate(
   
    onProgress = { isStarted ->
        // Handle loading state
    },
   
    onSuccess = { translation ->
        // Handle translated text
    },
   
    onFailed = { error ->
        // Handle failure
    }
)

4. Fetch Word Info with Callbacks

Retrieve dictionary information for a word using the wordInfo function with DictionaryListener callbacks:


kotlin

Copy code

translator.wordInfo(object : DictionaryListener {
    
    override fun onProgress(isStarted: Boolean) {
        // Handle loading state
    }

    override fun onSuccess(wordInfo: WordInfo) {
        // Handle word info
    }

    override fun onFailed(error: String?) {
        // Handle failure
    }
})

5. Fetch Word Info with Higher-Order Functions

You can also fetch word info using higher-order functions:


kotlin

Copy code

translator.wordInfo(
    
    onProgress = { isStarted ->
        // Handle loading state
    },
    
    onSuccess = { wordInfo ->
        // Handle word info
    },
    
    onFailed = { error ->
        // Handle failure
    }
)

Example Usage in an Activity

kotlin

Copy code

class MainActivity : AppCompatActivity() {
    private var translator: Translator? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Setup your UI and bindings

        // Example usage
        val text = "Hello"
        val sourceLanguage = "en"
        val targetLanguage = "es"

        translator = Translator(this, text, sourceLanguage, targetLanguage)

        translator?.translate(
            onProgress = { isStarted ->
                // Show loading
            },
            onSuccess = { translation ->
                // Display translation
            },
            onFailed = { error ->
                // Handle error
            }
        )
    }
}

Feedback

For suggestions or feedback, please contact us at: ahmed03160636141@gmail.com
