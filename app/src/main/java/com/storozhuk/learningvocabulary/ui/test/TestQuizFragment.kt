package com.storozhuk.learningvocabulary.ui.test

import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.storozhuk.learningvocabulary.R
import com.storozhuk.learningvocabulary.application.VocabularyContext
import com.storozhuk.learningvocabulary.dto.ui.TestQuestionAnswerDto
import com.storozhuk.learningvocabulary.service.TestQuizService

class TestQuizFragment : Fragment(R.layout.fragment_test_quiz) {

    private lateinit var fragmentView: View
    private lateinit var testQuizService: TestQuizService
    private lateinit var subject: String
    private var currentQuestionIndex: Int = 0
    private val args: TestQuizFragmentArgs by navArgs()

    private val subjectHeader = "Subject: "

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.fragmentView = view
        subject = args.subjectName
        testQuizService = (activity?.application as VocabularyContext).getTestQuizService()
        fragmentView.findViewById<TextView>(R.id.quiz_subject_name).text = String.format(subjectHeader + subject)
        initQuestion()
    }

    private fun initQuestion() {
        val testQuestions = testQuizService.getTestQuestions()
        val question = testQuestions[currentQuestionIndex]
        val testQuestionTitle = fragmentView.findViewById<TextView>(R.id.question)
        testQuestionTitle.text = question.question

        val answersGroup = fragmentView.findViewById<RadioGroup>(R.id.questions_group)
        for (i in question.answers.indices) {
            val answer = question.answers[i]
            val radioButton = answersGroup.getChildAt(i) as? RadioButton
            radioButton?.text = answer
        }

        val answerBtn = fragmentView.findViewById<android.widget.Button>(R.id.answer_btn)
        answerBtn.setOnClickListener {
            val checkedRadioButtonId = answersGroup.checkedRadioButtonId
            if (checkedRadioButtonId != -1) {
                val answer = fragmentView.findViewById<RadioButton>(checkedRadioButtonId)?.text
                val isCorrect = answer == question.correctAnswer

                val testAnswer = TestQuestionAnswerDto(
                    question = question.question,
                    answers = question.answers,
                    correctAnswer = question.correctAnswer,
                    chosenAnswer = answer?.toString() ?: "",
                    isCorrect = isCorrect
                )

                testQuizService.getTestAnswers().add(testAnswer)
                currentQuestionIndex++

                if (currentQuestionIndex < testQuestions.size) {
                    answersGroup.clearCheck()
                    initQuestion()
                } else {
                    val action = TestQuizFragmentDirections.actionQuizToResults(subject)
                    findNavController().navigate(action)
                }
            }
        }
    }
}