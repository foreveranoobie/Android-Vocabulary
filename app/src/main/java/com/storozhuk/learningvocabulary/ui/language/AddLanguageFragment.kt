package com.storozhuk.learningvocabulary.ui.language

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.storozhuk.learningvocabulary.R
import com.storozhuk.learningvocabulary.application.VocabularyContext
import com.storozhuk.learningvocabulary.db.repo.LanguagesRepository
import com.storozhuk.learningvocabulary.dto.LanguageDto
import com.storozhuk.learningvocabulary.ui.language.adapter.LanguagesCustomAdapter

class AddLanguageFragment: Fragment(R.layout.fragment_add_language) {
    private lateinit var languagesRepository: LanguagesRepository
    private lateinit var fragmentView: View

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.languagesRepository = (activity?.application as VocabularyContext).languagesRepository
        fragmentView = view
        fragmentView.findViewById<Button>(R.id.add_language_btn).setOnClickListener { addLanguage() }
        initLanguagesList()
    }

    private fun initLanguagesList(){
        System.err.println("Languages initialized")
        val languagesRecyclerView = fragmentView.findViewById<RecyclerView>(R.id.languages)
        val cursor = languagesRepository.fetch()
        val dataset = ArrayList<String>()
        cursor.moveToNext()
        while(!cursor.isAfterLast)
        {
            dataset.add(cursor.getString(1))
            cursor.moveToNext()
        }
        val languagesCustomAdapter = LanguagesCustomAdapter(dataset, languagesRepository)
        languagesRecyclerView.adapter = languagesCustomAdapter
        languagesRecyclerView.setLayoutManager(LinearLayoutManager(requireActivity()))
    }

    private fun addLanguage(){
        val newLanguageFieldInput = fragmentView.findViewById<EditText>(R.id.new_language_field)
        val language = newLanguageFieldInput.text.toString()
        if(language.isNotEmpty()){
            val languageDto = LanguageDto(null, language)
            languagesRepository.insert(languageDto)
            initLanguagesList()
            newLanguageFieldInput.setText("")
        }
    }
}