package com.codetech.api.dictionary

import com.google.gson.annotations.SerializedName

data class Phonetic(
    val text: String,
    val audio: String? = null
)

data class Definition(
    val definition: String,
    val example: String? = null,
    val synonyms: List<String>,
    val antonyms: List<String>
)

data class Meaning(
    @SerializedName("partOfSpeech") val partOfSpeech: String,
    @SerializedName("definitions") val definitions: List<Definition>
)

data class WordInfo(
    val word: String,
    val phonetic: String? = null,
    val phonetics: List<Phonetic>,
    val origin: String? = null,
    val meanings: List<Meaning>
)
