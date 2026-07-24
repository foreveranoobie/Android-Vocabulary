package com.storozhuk.learningvocabulary.ui.test

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.storozhuk.learningvocabulary.R
import com.storozhuk.learningvocabulary.application.VocabularyContext
import com.storozhuk.learningvocabulary.db.repo.SubjectsRepository
import com.storozhuk.learningvocabulary.service.LanguagesService
import com.storozhuk.learningvocabulary.service.SubjectsService
import com.storozhuk.learningvocabulary.service.TestQuizService
import com.storozhuk.learningvocabulary.service.TestsGenerator
import com.storozhuk.learningvocabulary.ui.common.spinner.LanguagesSpinnerAggregator
import com.storozhuk.learningvocabulary.ui.common.spinner.SubjectsSpinnerAggregator
import java.util.stream.Collectors

class TestFragment : Fragment(R.layout.fragment_test) {

    private lateinit var fragmentView: View

    private lateinit var testsGenerator: TestsGenerator
    private lateinit var languagesService: LanguagesService
    private lateinit var subjectsService: SubjectsService
    private lateinit var subjectsRepository: SubjectsRepository
    private lateinit var testQuizService: TestQuizService

    private lateinit var languagesSpinnerAggregator: LanguagesSpinnerAggregator
    private lateinit var subjectsSpinnerAggregator: SubjectsSpinnerAggregator

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.fragmentView = view
        this.testsGenerator =
            (activity?.application as VocabularyContext).getTestsGenerator()
        this.languagesService = (activity?.application as VocabularyContext).getLanguagesService()
        this.subjectsService = (activity?.application as VocabularyContext).getSubjectsService()
        this.subjectsRepository =
            (activity?.application as VocabularyContext).getSubjectsRepository()
        this.testsGenerator = (activity?.application as VocabularyContext).getTestsGenerator()
        this.testQuizService = (activity?.application as VocabularyContext).getTestQuizService()

        languagesSpinnerAggregator =
            LanguagesSpinnerAggregator(fragmentView.findViewById(R.id.test_languages_filter))
        this.subjectsSpinnerAggregator =
            SubjectsSpinnerAggregator(fragmentView.findViewById(R.id.test_subjects_filter))

        initStartButtonEvent()

        initLanguagesSpinner()
        initStartButtonEvent()
    }

    private fun initLanguagesSpinner() {
        val languages = languagesService.getAllLanguages().stream().map { it.language }
            .collect(Collectors.toList())

        languagesSpinnerAggregator.updateDataAndPutIntoActivity(languages, requireActivity())

        val onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>, view: View?, position: Int, id: Long
            ) {
                //Update list of subjects, if specific language is chosen
                updateSubjectsOnLanguageSelected()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
            }
        }

        languagesSpinnerAggregator.setOnItemSelectedListener(onItemSelectedListener)

    }

    private fun updateSubjectsOnLanguageSelected() {
        if (!languagesSpinnerAggregator.isFirstItemSelected()) {
            val selectedLanguageId = getLanguageIdFromSelectedInSpinner()

            val subjectList = subjectsService.getSubjectsForLanguage(selectedLanguageId)
            val subjectNames =
                subjectList.stream().map { it.subject!! }
                    .collect(Collectors.toList())
            subjectsSpinnerAggregator.updateDataAndPutIntoActivity(subjectNames, requireActivity())

            val onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>, view: View?, position: Int, id: Long
                ) {
                    //do nothing
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // Do nothing
                }
            }

            subjectsSpinnerAggregator.setOnItemSelectedListener(onItemSelectedListener)
        } else {
            subjectsSpinnerAggregator.updateAdapter(ArrayList(), requireActivity())
        }
    }

    private fun getLanguageIdFromSelectedInSpinner(): Int {
        val selectedLanguage = languagesSpinnerAggregator.getSelectedItemValue()
        return languagesService.getLanguageIdByName(selectedLanguage!!)
    }

    private fun initStartButtonEvent() {
        fragmentView.findViewById<Button>(R.id.start_test_btn).setOnClickListener {
            val languageId = getLanguageIdFromSelectedInSpinner()
            val subject = subjectsSpinnerAggregator.getSelectedItemValue()
            if (subject != null) {
                val testQuestions = testsGenerator.generateTestQuestions(languageId, subject)
                System.err.println("Test questions: $testQuestions")
                testQuizService.setTestQuestions(testQuestions)
                val action = TestFragmentDirections.actionTestToQuiz(subject)
                findNavController().navigate(action)
            }
        }
    }
}