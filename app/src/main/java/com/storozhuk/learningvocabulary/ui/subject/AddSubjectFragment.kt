package com.storozhuk.learningvocabulary.ui.subject

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.storozhuk.learningvocabulary.R
import com.storozhuk.learningvocabulary.application.VocabularyContext
import com.storozhuk.learningvocabulary.db.repo.LanguagesRepository
import com.storozhuk.learningvocabulary.db.repo.SubjectsRepository
import com.storozhuk.learningvocabulary.dto.data.SubjectDataDto
import com.storozhuk.learningvocabulary.ui.helper.UiHelper
import com.storozhuk.learningvocabulary.ui.home.helper.AllWordsFragmentHelper
import com.storozhuk.learningvocabulary.ui.subject.adapter.SubjectsCustomAdapter

class AddSubjectFragment : Fragment(R.layout.fragment_add_subject) {
    private lateinit var languagesRepository: LanguagesRepository
    private lateinit var subjectsRepository: SubjectsRepository
    private lateinit var fragmentView: View
    private var selectedLanguageId: Int = 1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.languagesRepository = (activity?.application as VocabularyContext).getLanguagesRepository()
        this.subjectsRepository = (activity?.application as VocabularyContext).getSubjectsRepository()
        fragmentView = view
        fragmentView.findViewById<Button>(R.id.add_subject_btn).setOnClickListener { addSubject() }
        initLanguagesSpinner()
    }

    private fun initLanguagesSpinner() {
        val languagesFilter = fragmentView.findViewById<Spinner>(R.id.subjects_languages_filter)
        var languages = ArrayList<String>()
        languagesRepository.fetch().use { cursor ->
            languages = AllWordsFragmentHelper.extractElementsFromCursorToArrayList(cursor,
                { ArrayList() },
                { cursor.getString(1) })
        }

        languagesFilter.adapter = AllWordsFragmentHelper.createDefaultDropdownDataAdapter(
            requireActivity(), languages
        )
        languagesFilter.isEnabled = true
        languagesFilter.isVisible = true

        languagesFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>, view: View?, position: Int, id: Long
            ) {
                selectedLanguageId = languagesRepository.fetchId(languages[position])
                //Update list of subjects if specific language is chosen
                updateSubjects()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
            }
        }
    }

    private fun addSubject() {
        if (selectedLanguageId > 1) {
            val newSubjectFieldInput = fragmentView.findViewById<EditText>(R.id.new_subject_field)
            val subject = newSubjectFieldInput.text.toString()
            if (subject.isNotEmpty() && subjectsRepository.fetchForSubjectAndLanguageId(
                    subject, selectedLanguageId
                ).count == 0
            ) {
                val subjectDataDto = SubjectDataDto(null, subject, selectedLanguageId)
                subjectsRepository.insert(subjectDataDto)
                newSubjectFieldInput.setText("")
                updateSubjects()
            }
        } else {
            UiHelper.showToast(fragmentView.context, "Select language")
        }
    }

    private fun updateSubjects() {
        if (selectedLanguageId > 1) {
            val subjectsRecyclerView = fragmentView.findViewById<RecyclerView>(R.id.subjects)
            val cursor = subjectsRepository.fetchForLanguageId(selectedLanguageId)
            val dataset = ArrayList<SubjectDataDto>()
            while (!cursor.isAfterLast) {
                dataset.add(SubjectDataDto(cursor.getInt(0), cursor.getString(1), selectedLanguageId))
                cursor.moveToNext()
            }
            val subjectsCustomAdapter =
                SubjectsCustomAdapter(dataset, subjectsRepository, requireActivity().window)
            subjectsRecyclerView.adapter = subjectsCustomAdapter
            subjectsRecyclerView.setLayoutManager(LinearLayoutManager(requireActivity()))
        } else {
            val subjectsRecyclerView = fragmentView.findViewById<RecyclerView>(R.id.subjects)
            val subjectsCustomAdapter =
                SubjectsCustomAdapter(arrayListOf(), subjectsRepository, requireActivity().window)
            subjectsRecyclerView.adapter = subjectsCustomAdapter
            subjectsRecyclerView.setLayoutManager(LinearLayoutManager(requireActivity()))

        }
    }
}