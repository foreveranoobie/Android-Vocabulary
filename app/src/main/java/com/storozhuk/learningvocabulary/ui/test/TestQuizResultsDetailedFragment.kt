package com.storozhuk.learningvocabulary.ui.test

import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.storozhuk.learningvocabulary.R
import com.storozhuk.learningvocabulary.application.VocabularyContext
import com.storozhuk.learningvocabulary.service.TestQuizService

class TestQuizResultsDetailedFragment : Fragment(R.layout.fragment_test_quiz_results_detailed) {
    private lateinit var fragmentView: View
    private lateinit var testQuizService: TestQuizService
    private var currentQuestionIndex: Int = 0


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.fragmentView = view
        testQuizService = (activity?.application as VocabularyContext).getTestQuizService()
        initQuestion()
    }

    private fun initQuestion() {
        resetRadioButtons()
        val question = testQuizService.getTestQuestions()[currentQuestionIndex]
        val answer = testQuizService.getTestAnswers()[currentQuestionIndex]
        val testQuestionTitle = fragmentView.findViewById<TextView>(R.id.question_result)
        testQuestionTitle.text = question.question

        val answersGroup = fragmentView.findViewById<RadioGroup>(R.id.questions_results_group)
        for (i in question.answers.indices) {
            val radioButton = answersGroup.getChildAt(i) as? RadioButton
            radioButton?.text = question.answers[i]
            if (answer.chosenAnswer == question.answers[i]) {
                radioButton?.isChecked = true
                radioButton?.setTypeface(null, android.graphics.Typeface.BOLD)
                if (answer.isCorrect) {
                    radioButton?.setBackgroundColor(android.graphics.Color.parseColor("#BF00FF00"))
                } else {
                    radioButton?.setBackgroundColor(android.graphics.Color.parseColor("#BFFF0000"))
                }
            } else {
                if (question.answers[i] == question.correctAnswer) {
                    radioButton?.setBackgroundColor(android.graphics.Color.parseColor("#BF00FF00"))
                }
            }
        }

        val nextBtn = fragmentView.findViewById<android.widget.Button>(R.id.next_question_btn)
        nextBtn.setOnClickListener {
            if (currentQuestionIndex < testQuizService.getTestQuestions().size - 1) {
                currentQuestionIndex++
                initQuestion()
            }
        }

        val prevBtn = fragmentView.findViewById<android.widget.Button>(R.id.previous_question_btn)
        prevBtn.setOnClickListener {
            if (currentQuestionIndex >= 1) {
                currentQuestionIndex--
                initQuestion()
            }
        }
    }

    private fun resetRadioButtons() {
        val answersGroup = fragmentView.findViewById<RadioGroup>(R.id.questions_results_group)
        for (i in 0 until answersGroup.childCount) {
            val radioButton = answersGroup.getChildAt(i) as? RadioButton
            radioButton?.isChecked = false
            radioButton?.setTypeface(null, android.graphics.Typeface.NORMAL)
            radioButton?.setBackgroundColor(android.graphics.Color.TRANSPARENT)
        }
    }
}