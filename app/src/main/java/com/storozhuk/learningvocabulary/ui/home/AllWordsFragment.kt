package com.storozhuk.learningvocabulary.ui.home

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.Spinner
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.core.view.get
import androidx.core.view.isVisible
import androidx.core.view.size
import androidx.fragment.app.Fragment
import com.storozhuk.learningvocabulary.R
import com.storozhuk.learningvocabulary.application.VocabularyContext
import com.storozhuk.learningvocabulary.db.repo.LanguagesRepository
import com.storozhuk.learningvocabulary.db.repo.WordsRepository
import com.storozhuk.learningvocabulary.dto.WordDto
import com.storozhuk.learningvocabulary.ui.home.helper.AllWordsFragmentHelper


class AllWordsFragment: Fragment(R.layout.fragment_all_words) {

    private lateinit var fragmentView: View
    private lateinit var wordsRepository: WordsRepository
    private lateinit var languagesRepository: LanguagesRepository
    private lateinit var wordsTable: TableLayout
    private var languagesList = ArrayList<String>()
    private var wordsList = ArrayList<WordDto>()
    private var selectedEditId: Int = 0
    private var selectedLanguageId: Int = 1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.fragmentView = view
        this.wordsRepository = (activity?.application as VocabularyContext).wordsRepository
        this.languagesRepository = (activity?.application as VocabularyContext).languagesRepository
        wordsTable = view.findViewById(R.id.words_table)
        updateRows()
        updateLanguageSpinner()
    }

    private fun updateLanguageSpinner() {
        val languagesFilter = fragmentView.findViewById<Spinner>(R.id.languages_filter)
        languagesList.clear()
        val cursor = languagesRepository.fetch()
        while(!cursor.isAfterLast){
            languagesList.add(cursor.getString(1))
            cursor.moveToNext()
        }
        cursor.close()

        val dataAdapter = ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_item, languagesList)
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        languagesFilter.adapter = dataAdapter
        languagesFilter.isEnabled = true
        languagesFilter.isVisible = true

        languagesFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedLanguageId = languagesRepository.fetchId(languagesList[position])
                updateRows()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
            }
        }
    }

    override fun onStart() {
        super.onStart()
        requireActivity().findViewById<Button>(R.id.add_word_btn).setOnClickListener(this::showAddWordPopup)
    }

    private fun updateRows(){
        cleanTable()
        wordsList = ArrayList()
        val cursor = if(selectedLanguageId == 1) wordsRepository.fetch()
            else wordsRepository.fetchForLanguage(selectedLanguageId)
        var index = 0
        while(!cursor.isAfterLast){
            val word = WordDto(null, cursor.getString(1), cursor.getString(2), cursor.getString(3),
                selectedLanguageId)
            insertRow(word, index++)
            wordsList.add(word)
            cursor.moveToNext()
        }
    }

    private fun insertRow(wordDto: WordDto, indexNum: Int){
        val tableRow = TableRow(fragmentView.context)
        tableRow.layoutParams = TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT)

        //Original columns
        val original = AllWordsFragmentHelper.createTextView(fragmentView.context, AllWordsFragmentHelper.separateTextIntoRows(wordDto.original))
        val originalLayout = AllWordsFragmentHelper.createLinearLayout(fragmentView.context, original)

        //Translation column
        val translation = AllWordsFragmentHelper.createTextView(fragmentView.context, AllWordsFragmentHelper.separateTextIntoRows(wordDto.translate))
        val translationLayout = AllWordsFragmentHelper.createLinearLayout(fragmentView.context, translation)

        //Subject column
        tableRow.addView(originalLayout)
        tableRow.addView(translationLayout)
        tableRow.setOnLongClickListener{showEditWordPopup(fragmentView, indexNum)}

        wordsTable.addView(tableRow)
        fragmentView.refreshDrawableState()
    }

    /**
     * Shows popup to word edit and removal
     */
    private fun showEditWordPopup(view: View, wordIndex: Int): Boolean{
        val wordDto = wordsList[wordIndex]
        val originalText = wordDto.original
        val translatedText = wordDto.translate
        val subjectText = wordDto.subject

        selectedEditId = wordsRepository.findIdByOriginal(originalText)
        val inflater = LayoutInflater.from(context)
        val removeWordPopup = inflater.inflate(com.storozhuk.learningvocabulary.R.layout.remove_word_popup, null)

        val width = LinearLayout.LayoutParams.WRAP_CONTENT
        val height = LinearLayout.LayoutParams.WRAP_CONTENT
        val focusable = true // lets taps outside the popup also dismiss it
        val popupWindow = PopupWindow(removeWordPopup, width, height, focusable)
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0)

        removeWordPopup.findViewById<EditText>(R.id.word_original_input_edit).setText(originalText)
        removeWordPopup.findViewById<EditText>(R.id.word_translated_input_edit).setText(translatedText)
        removeWordPopup.findViewById<EditText>(R.id.word_subject_input_edit).setText(subjectText)

        removeWordPopup.findViewById<Button>(R.id.update_btn).setOnClickListener{updateWord(removeWordPopup)}
        removeWordPopup.findViewById<Button>(R.id.delete_word_btn).setOnClickListener{deleteWord()}
        removeWordPopup.findViewById<ImageButton>(R.id.close_window_btn_edit).setOnClickListener {
            popupWindow.dismiss()
        }
        updateLanguageSpinnerOnEditWordPopup(removeWordPopup)
        return true
    }

    /**
     * Shows popup to new word creation
     */
    private fun showAddWordPopup(view: View) {
        val inflater = LayoutInflater.from(context)
        val addWordPopup = inflater.inflate(R.layout.add_word_popup, null)

        val width = LinearLayout.LayoutParams.WRAP_CONTENT
        val height = LinearLayout.LayoutParams.WRAP_CONTENT
        val focusable = true // lets taps outside the popup also dismiss it
        val popupWindow = PopupWindow(addWordPopup, width, height, focusable)
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0)
        addWordPopup.findViewById<Button>(R.id.add_btn).setOnClickListener{ addWord(addWordPopup) }
        addWordPopup.findViewById<ImageButton>(R.id.close_window_btn).setOnClickListener {
            popupWindow.dismiss()
            updateRows()
        }

        val languagesFilter = addWordPopup.findViewById<Spinner>(R.id.language_option)
        val dataAdapter = ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_item, languagesList)
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        languagesFilter.adapter = dataAdapter
    }

    private fun updateLanguageSpinnerOnEditWordPopup(removeWordPopup: View) {
        val languagesFilter = removeWordPopup.findViewById<Spinner>(R.id.update_lang_spinner)
        val languageValue = wordsRepository.findLanguageValueForWord(selectedEditId)

        val dataAdapter = ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_item, languagesList)
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        languagesFilter.adapter = dataAdapter
        languagesFilter.isEnabled = true
        languagesFilter.isVisible = true
        languagesFilter.setSelection(languagesList.indexOf(languageValue), false)
    }


    private fun addWord(view: View) {
        val original = view.findViewById<EditText>(R.id.word_original_input).text.toString()
        if (original.isNotEmpty())
        {
            val translated = view.findViewById<EditText>(R.id.word_translated_input).text.toString()
            val subject = view.findViewById<EditText>(R.id.word_subject_input).text.toString()
            val languageId = languagesRepository.fetchId(view.findViewById<Spinner>(R.id.language_option).selectedItem.toString())
            val wordDto = WordDto(null, original, translated, subject, languageId)
            if (wordsRepository.insert(wordDto) >= 0) {
                view.findViewById<EditText>(R.id.word_original_input).setText("")
                view.findViewById<EditText>(R.id.word_translated_input).setText("")
                view.findViewById<EditText>(R.id.word_subject_input).setText("")
            }
        }
    }

    private fun updateWord(popupView: View) {
        val original = popupView.findViewById<EditText>(R.id.word_original_input_edit).text.toString()
        if (original.isNotEmpty())
        {
            val translated = popupView.findViewById<EditText>(R.id.word_translated_input_edit).text.toString()
            val subject = popupView.findViewById<EditText>(R.id.word_subject_input_edit).text.toString()
            val language = popupView.findViewById<Spinner>(R.id.update_lang_spinner).selectedItem.toString()
            var languageId = languagesRepository.fetchId(language)
            if(languageId < 0){
                languageId = 0
            }
            val wordDto = WordDto(selectedEditId, original, translated, subject, languageId)
            if(wordsRepository.update(wordDto) >= 0){
                updateRows()
            }
        }
    }

    private fun deleteWord(){
        wordsRepository.delete(selectedEditId)
        updateRows()
    }

    private fun cleanTable(){
        val wordsTable = fragmentView.findViewById<TableLayout>(R.id.words_table)
        wordsTable.removeViews(1, wordsTable.size - 1)
    }
}