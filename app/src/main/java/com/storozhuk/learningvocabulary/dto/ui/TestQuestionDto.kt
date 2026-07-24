package com.storozhuk.learningvocabulary.dto.ui

open class TestQuestionDto(val question: String, val answers: List<String>, val correctAnswer: String) {
    override fun toString(): String {
        return "TestQuestionDto(question='$question', answers=$answers, correctAnswer='$correctAnswer')"
    }
}