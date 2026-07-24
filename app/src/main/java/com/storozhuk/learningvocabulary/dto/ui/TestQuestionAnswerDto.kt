package com.storozhuk.learningvocabulary.dto.ui

class TestQuestionAnswerDto(
    question: String,
    answers: List<String>,
    correctAnswer: String,
    var chosenAnswer: String,
    var isCorrect: Boolean
) : TestQuestionDto(question, answers, correctAnswer) {

}