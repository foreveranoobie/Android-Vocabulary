package com.storozhuk.learningvocabulary.service

import com.storozhuk.learningvocabulary.dto.ui.TestQuestionAnswerDto
import com.storozhuk.learningvocabulary.dto.ui.TestQuestionDto
import java.util.LinkedList

class TestQuizService {
    private val testQuestions = ArrayList<TestQuestionDto>()
    private lateinit var testAnswers: ArrayList<TestQuestionAnswerDto>

    fun setTestQuestions(questions: List<TestQuestionDto>) {
        testQuestions.clear()
        testQuestions.addAll(questions)
        testAnswers = ArrayList(questions.size)
    }

    fun getTestQuestions(): List<TestQuestionDto> {
        return testQuestions
    }

    fun getTestAnswers(): ArrayList<TestQuestionAnswerDto> {
        return testAnswers
    }
}