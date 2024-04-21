package com.storozhuk.learningvocabulary.dto

data class WordDto(val id: Int?, val original: String, val translate: String?, val subject: String?,
    val languageId : Int)
