Translator Library
The Translator Library provides functionality to translate text using the Google Translate API via WorkManager. 
It supports both callback-based and higher-order function approaches for handling translation results.

Features
Translate text using Google Translate API.
Handle loading, translation success, and failure through callbacks or higher-order functions.
Use WorkManager for background translation tasks.
Setup
Add Internet Permission

Ensure you have internet permission in your AndroidManifest.xml:

xml
Copy code
<uses-permission android:name="android.permission.INTERNET" />
Usage
1. Create a Translator Instance
   Initialize the Translator with context, text to translate, source language, and target language.

kotlin
Copy code
val translator = Translator(context, text, sourceLanguage, targetLanguage)
2. Translate with Callbacks
   Use the translate function with TranslationListener callbacks.

kotlin
Copy code
translator.translate(object : TranslationListener {
override fun onLoading(isStarted: Boolean) {
// Handle loading state
}

    override fun onTranslated(translation: String?) {
        // Handle translated text
    }

    override fun onFailed(error: String?) {
        // Handle failure
    }
})
3. Translate with Higher-Order Functions
   Alternatively, use the translate function with higher-order function parameters.

kotlin
Copy code
translator.translate(
onLoading = { isStarted ->
// Handle loading state
},
onTranslated = { translation ->
// Handle translated text
},
onFailed = { error ->
// Handle failure
}
)
Example Usage in an Activity
kotlin
Copy code
class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private val languages = listOf("en", "ar", "ur", "es", "fr", "de", "it")
    private var translator: Translator? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupSpinners()

        binding.translateBtn.setOnClickListener {
            translateWithHigherOrderFunction()
        }
    }

    private fun translateWithHigherOrderFunction() {
        if (binding.translateEd.text.toString().isNotBlank()) {
            val text = binding.translateEd.text.toString().trim()
            val sourceLanguage = binding.sourceLanguageSpinner.selectedItem.toString()
            val targetLanguage = binding.targetLanguageSpinner.selectedItem.toString()

            translator = Translator(this, text, sourceLanguage, targetLanguage)

            translator?.translate(
                onLoading = { isStarted ->
                    binding.loadingIndicator.isVisible = isStarted
                },
                onTranslated = { translation ->
                    binding.tv.text = translation
                },
                onFailed = { error ->
                    Log.d("mainInfo", "onFailed: $error")
                }
            )
        } else {
            Toast.makeText(this, "Enter text to translate", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupSpinners() {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, languages)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        binding.sourceLanguageSpinner.adapter = adapter
        binding.targetLanguageSpinner.adapter = adapter
    }
}
Feedback
For suggestions or feedback, please contact us at: ahmed03160636141@gmail.com

