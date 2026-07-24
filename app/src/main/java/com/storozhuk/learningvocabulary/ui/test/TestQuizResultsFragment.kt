package com.storozhuk.learningvocabulary.ui.test

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.storozhuk.learningvocabulary.R
import com.storozhuk.learningvocabulary.application.VocabularyContext
import com.storozhuk.learningvocabulary.service.TestQuizService

class TestQuizResultsFragment : Fragment(R.layout.fragment_test_results_overview) {
    private lateinit var quizService: TestQuizService
    private lateinit var subject: String

    private val args: TestQuizFragmentArgs by navArgs()

    private val subjectHeader = "Results for subject: "

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subject = args.subjectName
        // Retrieve quizService from the application context
        quizService = (activity?.application as VocabularyContext).getTestQuizService()
        view.findViewById<TextView>(R.id.quiz_results_subject_name).text = String.format(subjectHeader + subject)

        initResultsUi()
    }

    private fun initResultsUi() {
        // Calculate the score
        val testAnswers = quizService.getTestAnswers()
        val correctAnswers = testAnswers.count { it.isCorrect }
        val totalQuestions = testAnswers.size

        // Display the score in the TextView
        val resultScoreTextView = view?.findViewById<TextView>(R.id.result_score)
        if (resultScoreTextView != null) {
            resultScoreTextView.text = "Your score is $correctAnswers/$totalQuestions"
        }

        // Add empty OnClickListener for the button
        view?.findViewById<Button>(R.id.see_detailed_btn)?.setOnClickListener {
            findNavController().navigate(R.id.action_results_to_detailed)
        }
    }
}