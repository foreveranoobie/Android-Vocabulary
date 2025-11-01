package com.storozhuk.learningvocabulary.ui.home

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.Spinner
import android.widget.TableLayout
import android.widget.TableRow
import androidx.core.view.isVisible
import androidx.core.view.size
import androidx.fragment.app.Fragment
import com.storozhuk.learningvocabulary.R
import com.storozhuk.learningvocabulary.application.VocabularyContext
import com.storozhuk.learningvocabulary.db.repo.LanguagesRepository
import com.storozhuk.learningvocabulary.db.repo.WordsRepository
import com.storozhuk.learningvocabulary.dto.WordDto
import com.storozhuk.learningvocabulary.ui.helper.UiHelper.Companion.cursorHasString
import com.storozhuk.learningvocabulary.ui.helper.UiHelper.Companion.dimBackground
import com.storozhuk.learningvocabulary.ui.home.helper.AllWordsFragmentHelper


class AllWordsFragment : Fragment(R.layout.fragment_all_words) {

    private lateinit var fragmentView: View
    private lateinit var wordsRepository: WordsRepository
    private lateinit var languagesRepository: LanguagesRepository
    private lateinit var wordsTable: TableLayout
    private var languagesList = ArrayList<String>()
    private var subjectsList = ArrayList<String>()
    private var wordsList = ArrayList<WordDto>()
    private var selectedEditId: Int = 0
    private var selectedLanguageId: Int = 1
    private var selectedSubject: String? = null

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
        languagesRepository.fetch().use { cursor ->
            while (!cursor.isAfterLast) {
                languagesList.add(cursor.getString(1))
                cursor.moveToNext()
            }
        }

        var dataAdapter =
            ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_item, languagesList)
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        languagesFilter.adapter = dataAdapter
        languagesFilter.isEnabled = true
        languagesFilter.isVisible = true

        languagesFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedLanguageId = languagesRepository.fetchId(languagesList[position])
                //Update list of subjects, if specific language is chosen
                updateSubjects(selectedLanguageId)

                updateRows()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
            }
        }
    }

    private fun updateSubjects(selectedLanguageId: Int) {
        if (selectedLanguageId != 1) {
            val subjectsFilter = fragmentView.findViewById<Spinner>(R.id.subjects_filter)
            subjectsList.clear()
            wordsRepository.fetchSubjectsForLanguage(selectedLanguageId).use { cursor ->
                while (!cursor.isAfterLast) {
                    subjectsList.add(cursor.getString(0))
                    cursor.moveToNext()
                }
            }

            val dataAdapter =
                ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_item, subjectsList)
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            subjectsFilter.adapter = dataAdapter
            subjectsFilter.isEnabled = true
            subjectsFilter.isVisible = true
            subjectsFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    selectedSubject = subjectsList[position]
                    updateRowsHavingSubject()
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // Do nothing
                }
            }
            if(selectedSubject != null) {
                subjectsFilter.setSelection(subjectsList.indexOf(selectedSubject))
            }
        } else {
            selectedSubject = null
            val subjectsFilter = fragmentView.findViewById<Spinner>(R.id.subjects_filter)
            val dataAdapter =
                ArrayAdapter(
                    requireActivity(), android.R.layout.simple_spinner_item,
                    ArrayList<String>()
                )
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            subjectsFilter.adapter = dataAdapter
        }
    }

    override fun onStart() {
        super.onStart()
        requireActivity().findViewById<Button>(R.id.add_word_btn)
            .setOnClickListener(this::showAddWordPopup)
    }

    private fun updateRows() {
        cleanTable()
        wordsList = ArrayList()
        val cursor = if (selectedLanguageId == 1) wordsRepository.fetch()
        else wordsRepository.fetchForLanguage(selectedLanguageId)
        cursor.use {
            var index = 0
            while (!cursor.isAfterLast) {
                val word = WordDto(
                    null, cursor.getString(1), cursor.getString(2), cursor.getString(3),
                    selectedLanguageId
                )
                insertRow(word, index++)
                wordsList.add(word)
                cursor.moveToNext()
            }
        }
    }

    private fun updateRowsHavingSubject() {
        cleanTable()
        wordsList = ArrayList()
        if (selectedLanguageId != 1 && selectedSubject != null) {
            wordsRepository.fetchForLanguageAndSubject(selectedLanguageId, selectedSubject!!)
                .use { cursor ->
                    var index = 0
                    while (!cursor.isAfterLast) {
                        val word = WordDto(
                            null, cursor.getString(1), cursor.getString(2), cursor.getString(3),
                            selectedLanguageId
                        )
                        insertRow(word, index++)
                        wordsList.add(word)
                        cursor.moveToNext()
                    }
                }
        }
    }

    private fun insertRow(wordDto: WordDto, indexNum: Int) {
        val tableRow = TableRow(fragmentView.context)
        tableRow.layoutParams = TableLayout.LayoutParams(
            TableLayout.LayoutParams.MATCH_PARENT,
            TableLayout.LayoutParams.WRAP_CONTENT
        )
        val originalParams = TableRow.LayoutParams(
            TableRow.LayoutParams.WRAP_CONTENT,
            TableRow.LayoutParams.MATCH_PARENT,
            1f
        )

        //Original columns
        val original = AllWordsFragmentHelper.createTextView(
            fragmentView.context,
            AllWordsFragmentHelper.separateTextIntoRows(wordDto.original)
        )
        original.layoutParams = originalParams
        original.setBackgroundResource(R.drawable.table_item_left_elem)
        original.setPadding(5.dpToPx(fragmentView.context), 0, 0, 0)

        //Translation column
        val translation = AllWordsFragmentHelper.createTextView(
            fragmentView.context,
            AllWordsFragmentHelper.separateTextIntoRows(wordDto.translate)
        )
        translation.layoutParams = originalParams
        translation.setBackgroundResource(R.drawable.table_item_right_elem)
        translation.setPadding(5.dpToPx(fragmentView.context), 0, 0, 0)

        //Subject column
        tableRow.layoutParams = TableRow.LayoutParams(
            TableRow.LayoutParams.MATCH_PARENT,
            TableRow.LayoutParams.MATCH_PARENT,
            0f
        )
        tableRow.setPadding(0, 0, 0, 5)
        tableRow.setOnClickListener { showEditWordPopup(fragmentView, indexNum) }
        tableRow.setBackgroundColor(Color.parseColor("#CCCCCC"))
        tableRow.addView(original)
        tableRow.addView(translation)
        wordsTable.addView(tableRow)
        fragmentView.refreshDrawableState()
    }

    /**
     * Shows popup to word edit and removal
     */
    private fun showEditWordPopup(view: View, wordIndex: Int): Boolean {
        val wordDto = wordsList[wordIndex]
        val originalText = wordDto.original
        val translatedText = wordDto.translate
        val subjectText = wordDto.subject

        selectedEditId = wordsRepository.findIdByOriginal(originalText)
        val inflater = LayoutInflater.from(context)
        val removeWordPopup =
            inflater.inflate(R.layout.remove_word_popup, null)

        val width = LinearLayout.LayoutParams.WRAP_CONTENT
        val height = LinearLayout.LayoutParams.WRAP_CONTENT
        val focusable = true // lets taps outside the popup also dismiss it
        val popupWindow = PopupWindow(removeWordPopup, width, height, focusable)
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0)
        popupWindow.setOnDismissListener {
            dimBackground(requireActivity().window, 0f) // Remove dim when dismissed
        }

        dimBackground(requireActivity().window, 0.5f) // Add background dim

        removeWordPopup.findViewById<EditText>(R.id.word_original_input_edit).setText(originalText)
        removeWordPopup.findViewById<EditText>(R.id.word_translated_input_edit)
            .setText(translatedText)
        removeWordPopup.findViewById<EditText>(R.id.word_subject_input_edit).setText(subjectText)

        removeWordPopup.findViewById<Button>(R.id.update_btn)
            .setOnClickListener {
                updateWord(removeWordPopup)
                popupWindow.dismiss()
            }
        removeWordPopup.findViewById<Button>(R.id.delete_word_btn)
            .setOnClickListener {
                deleteWord(removeWordPopup)
                popupWindow.dismiss()
            }
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
        addWordPopup.findViewById<Button>(R.id.add_btn).setOnClickListener {
            addWord(addWordPopup)
            updateRows()
            updateSubjects(selectedLanguageId)
            popupWindow.dismiss()
        }
        addWordPopup.findViewById<ImageButton>(R.id.close_window_btn).setOnClickListener {
            popupWindow.dismiss()
        }

        popupWindow.setOnDismissListener {
            dimBackground(requireActivity().window, 0f) // Remove dim when dismissed
        }

        dimBackground(requireActivity().window, 0.5f) // Add background dim

        val languagesFilter = addWordPopup.findViewById<Spinner>(R.id.language_option)
        val dataAdapter =
            ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_item, languagesList)
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        languagesFilter.adapter = dataAdapter

        languagesFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (position != 0) {
                    val languageIdToFetchFor = languagesRepository.fetchId(languagesList[position])
                    val subjectsForLanguage = ArrayList<String>()
                    wordsRepository.fetchSubjectsForLanguage(languageIdToFetchFor)
                        .use { cursor ->
                            while (!cursor.isAfterLast) {
                                subjectsForLanguage.add(cursor.getString(0))
                                cursor.moveToNext()
                            }
                        }
                    val subjectsInput =
                        addWordPopup.findViewById<AutoCompleteTextView>(R.id.word_subject_input)
                    val subjectsDataAdapter = ArrayAdapter(
                        requireActivity(), android.R.layout.select_dialog_item,
                        subjectsForLanguage
                    )
                    subjectsInput.threshold = 1
                    subjectsInput.setAdapter(subjectsDataAdapter)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
            }
        }
    }

    private fun updateLanguageSpinnerOnEditWordPopup(removeWordPopup: View) {
        val languagesFilter = removeWordPopup.findViewById<Spinner>(R.id.update_lang_spinner)
        val languageValue = wordsRepository.findLanguageValueForWord(selectedEditId)

        val dataAdapter =
            ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_item, languagesList)
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        languagesFilter.adapter = dataAdapter
        languagesFilter.isEnabled = true
        languagesFilter.isVisible = true
        languagesFilter.setSelection(languagesList.indexOf(languageValue), false)
    }


    private fun addWord(view: View) {
        val original = view.findViewById<EditText>(R.id.word_original_input).text.toString()
        if (original.isNotEmpty()) {
            val translated = view.findViewById<EditText>(R.id.word_translated_input).text.toString()
            val subject = view.findViewById<EditText>(R.id.word_subject_input).text.toString()
            selectedSubject = subject
            val languageId =
                languagesRepository.fetchId(view.findViewById<Spinner>(R.id.language_option).selectedItem.toString())
            selectedLanguageId = languageId
            val wordDto = WordDto(null, original, translated, subject, languageId)
            wordsRepository.insert(wordDto)
        }
    }

    private fun updateWord(popupView: View) {
        val original =
            popupView.findViewById<EditText>(R.id.word_original_input_edit).text.toString()
        if (original.isNotEmpty()) {
            val translated =
                popupView.findViewById<EditText>(R.id.word_translated_input_edit).text.toString()
            val subject =
                popupView.findViewById<EditText>(R.id.word_subject_input_edit).text.toString()
            val language =
                popupView.findViewById<Spinner>(R.id.update_lang_spinner).selectedItem.toString()
            var languageId = languagesRepository.fetchId(language)
            if (languageId < 0) {
                languageId = 0
            }
            val wordDto = WordDto(selectedEditId, original, translated, subject, languageId)
            if (wordsRepository.update(wordDto) >= 0) {
                selectedLanguageId = languageId
                selectedSubject = subject
                updateSubjects(selectedLanguageId)
            }
        }
    }

    private fun deleteWord(removeWordPopup: View) {
        wordsRepository.delete(selectedEditId)
        val subject =
            removeWordPopup.findViewById<EditText>(R.id.word_subject_input_edit).text.toString()
        val language =
            removeWordPopup.findViewById<Spinner>(R.id.update_lang_spinner).selectedItem.toString()
        selectedLanguageId = languagesRepository.fetchId(language)
        //Reset subjects index if the word was the last one for the subject
        val subjectsForLanguage = wordsRepository.fetchSubjectsForLanguage(selectedLanguageId)
        if (subjectsForLanguage.count == 0) {
            updateRows()
            selectedSubject = null
        } else if (cursorHasString(subjectsForLanguage, subject)){
            selectedSubject = subject
        }
        updateSubjects(selectedLanguageId)
    }

    private fun cleanTable() {
        val wordsTable = fragmentView.findViewById<TableLayout>(R.id.words_table)
        wordsTable.removeViews(1, wordsTable.size - 1)
    }

    private fun Int.dpToPx(context: Context): Int {
        return (this * context.resources.displayMetrics.density).toInt()
    }
}