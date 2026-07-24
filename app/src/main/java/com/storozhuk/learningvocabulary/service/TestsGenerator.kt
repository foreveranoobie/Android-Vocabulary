package com.storozhuk.learningvocabulary.service

import android.database.Cursor
import com.storozhuk.learningvocabulary.db.repo.WordsRepository
import com.storozhuk.learningvocabulary.dto.ui.TestQuestionDto

class TestsGenerator(val wordsRepository: WordsRepository) {

    companion object {
        private const val NUMBER_OF_QUESTIONS = 10
    }

    fun generateTestQuestions(languageId: Int, subject: String): List<TestQuestionDto> {
        val cursor = wordsRepository.fetchForLanguageAndSubject(languageId, subject)
        var wordsMap = extractWordsFromCursor(cursor)
        wordsMap = reduceMapToTenRandomElements(wordsMap)
        val testQuestions = ArrayList<TestQuestionDto>(NUMBER_OF_QUESTIONS)
        for(word in wordsMap.entries) {
            val question = word.key
            val correctAnswer = word.value
            if(correctAnswer.isNotEmpty()) {
                val allAnswers = wordsMap.values.toList()
                val answers = generateRandomAnswers(correctAnswer, allAnswers)
                val testQuestion = TestQuestionDto(question, answers, correctAnswer)
                testQuestions.add(testQuestion)
            }
        }
        return testQuestions
    }

    private fun extractWordsFromCursor(cursor: Cursor): Map<String, String> {
        val wordsMap = mutableMapOf<String, String>()
        try {
            while (!cursor.isAfterLast) {
                val original = cursor.getString(1) // Assuming column 1 is 'original'
                val translation = cursor.getString(2) // Assuming column 2 is 'translation'
                wordsMap[original] = translation
                cursor.moveToNext()
            }
        } finally {
            cursor.close()
        }
        return wordsMap
    }

    private fun reduceMapToTenRandomElements(wordsMap: Map<String, String>): Map<String, String> {
        return wordsMap.entries.shuffled().take(NUMBER_OF_QUESTIONS).associate { it.key to it.value }
    }

    private fun generateRandomAnswers(correctAnswer: String, allAnswers: List<String>): List<String> {
        val answersPool = allAnswers.toMutableList()
        answersPool.remove(correctAnswer) // Remove the correct answer to avoid duplicates
        val randomAnswers = answersPool.shuffled().take(3).toMutableList() // Take 3 random incorrect answers
        randomAnswers.add(correctAnswer) // Add the correct answer
        return randomAnswers.shuffled() // Shuffle the final list to randomize the order
    }
}