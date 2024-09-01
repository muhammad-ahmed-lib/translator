package com.codetech.translator

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.codetech.api.Translator
import com.codetech.api.dictionary.DictionaryListener
import com.codetech.api.dictionary.WordInfo
import com.codetech.api.translation.TranslationListener
import com.codetech.translator.databinding.ActivityMainBinding

fun Activity.hideKeyboard() {
    hideKeyboard(currentFocus ?: View(this))
}

fun Context.hideKeyboard(view: View) {
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}
class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private val TAG = "mainInfo"
    private val languages = listOf("en","ar", "ur", "es", "fr", "de", "it")

    private var translator: Translator?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupSpinners()

        binding.translateEd.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)) {
                translateWithHigherOrderFunction()
                true
            } else {
                false
            }
        }

        binding.translateEd.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                if (s.toString().isNotBlank()){
                    binding.crossBtn.visibility=View.VISIBLE
                    binding.crossBtn.setOnClickListener {
                        binding.translateEd.setText("")
                    }
                }else{
                    binding.crossBtn.visibility=View.GONE
                }
            }

        })


        binding.translateBtn.setOnClickListener {
            translateInterfaceCallBacks()
        }
        binding.wordInfoBtn.setOnClickListener {
        }
    }
    private fun fetchWordInfo(){
        if (binding.translateEd.text.toString().isNotBlank()){
            val word = binding.translateEd.text.toString().trim()
            hideKeyboard()
            translator= Translator(this, word = word)

            translator?.wordInfo(object : DictionaryListener {
                override fun onProgress(isStarted: Boolean) {
                    binding.infoLoadingIndicator.isVisible=isStarted
                }

                override fun onSuccess(wordInfo: WordInfo) {
                    binding.wordInfoTv.append(wordInfo.toString())
                }

                override fun onFailed(error: String?) {
                    Log.d(TAG, "fetchWordInfo failed: $error")
                }

            })

        }else{
            Toast.makeText(this, "Enter text to get info", Toast.LENGTH_SHORT).show()
        }
    }
    private fun wordInfoWithHigherOrderFunction(){
        if (binding.translateEd.text.toString().isNotBlank()){
            val text = binding.translateEd.text.toString().trim()

            translator= Translator(this, text)


            translator?.wordInfo(
                onProgress = {isStarted->
                    binding.infoLoadingIndicator.isVisible=isStarted
                },
                onSuccess = {wordInfo->
                    binding.tv.append(wordInfo.toString())
                },
                onFailed = {error->
                    Log.d(TAG, "onFailed: $error")
                }
            )

        }else{
            Toast.makeText(this, "Enter text to translate", Toast.LENGTH_SHORT).show()
        }
    }
    private fun println(m:String){
        Log.d(TAG, "println: $m")
    }
    private fun translateInterfaceCallBacks(){
        if (binding.translateEd.text.toString().isNotBlank()){
            hideKeyboard()
            val text = binding.translateEd.text.toString().trim()
            val sourceLanguage = binding.sourceLanguageSpinner.selectedItem.toString()
            val targetLanguage = binding.targetLanguageSpinner.selectedItem.toString()

            translator= Translator(context = this, text,sourceLanguage,targetLanguage)


            translator?.translate(object: TranslationListener {
                override fun onProgress(isStarted: Boolean) {
                    binding.loadingIndicator.isVisible=isStarted
                }

                override fun onSuccess(translation: String?) {
                    binding.tv.text=translation
                }

                override fun onFailed(error: String?) {
                    Log.d(TAG, "onFailed: $error")
                }

            })

        }else{
            Toast.makeText(this, "Enter text to translate", Toast.LENGTH_SHORT).show()
        }
    }

    private fun translateWithHigherOrderFunction(){
        if (binding.translateEd.text.toString().isNotBlank()){
            val text = binding.translateEd.text.toString().trim()
            val sourceLanguage = binding.sourceLanguageSpinner.selectedItem.toString()
            val targetLanguage = binding.targetLanguageSpinner.selectedItem.toString()

            translator= Translator(this, text, sourceLanguage, targetLanguage)


            translator?.translate(
                onProgress = {isStarted->
                    binding.loadingIndicator.isVisible=isStarted
                },
                onSuccess = {translation->
                    binding.tv.text=translation
                },
                onFailed = {error->
                    Log.d(TAG, "onFailed: $error")
                }
            )

        }else{
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
