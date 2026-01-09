package com.storozhuk.learningvocabulary.ui.home

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.Spinner
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.core.view.size
import androidx.fragment.app.Fragment
import com.storozhuk.learningvocabulary.R
import com.storozhuk.learningvocabulary.application.VocabularyContext
import com.storozhuk.learningvocabulary.db.repo.LanguagesRepository
import com.storozhuk.learningvocabulary.db.repo.SubjectsRepository
import com.storozhuk.learningvocabulary.db.repo.WordsRepository
import com.storozhuk.learningvocabulary.dto.data.WordDataDto
import com.storozhuk.learningvocabulary.dto.ui.WordDto
import com.storozhuk.learningvocabulary.ui.helper.UiHelper.Companion.clearEditText
import com.storozhuk.learningvocabulary.ui.helper.UiHelper.Companion.dimBackground
import com.storozhuk.learningvocabulary.ui.helper.UiHelper.Companion.showToast
import com.storozhuk.learningvocabulary.ui.home.helper.AllWordsFragmentHelper
import com.storozhuk.learningvocabulary.ui.home.helper.AllWordsFragmentHelper.Companion.createDefaultDropdownDataAdapter
import com.storozhuk.learningvocabulary.ui.home.helper.AllWordsFragmentHelper.Companion.extractElementsFromCursorToArrayList
import com.storozhuk.learningvocabulary.ui.home.spinner.LanguagesSpinnerAggregator
import com.storozhuk.learningvocabulary.ui.home.spinner.SubjectsSpinnerAggregator

class AllWordsFragment : Fragment(R.layout.fragment_all_words) {

    private lateinit var fragmentView: View
    private lateinit var wordsRepository: WordsRepository
    private lateinit var languagesRepository: LanguagesRepository
    private lateinit var subjectsRepository: SubjectsRepository
    private lateinit var wordsTable: TableLayout
    private lateinit var addWordPopupView: View
    private lateinit var addWordPopupWindow: PopupWindow
    private lateinit var editWordPopupView: View
    private lateinit var editWordPopupWindow: PopupWindow
    private lateinit var languagesSpinnerAggregator: LanguagesSpinnerAggregator
    private lateinit var subjectsSpinnerAggregator: SubjectsSpinnerAggregator
    private var wordsList = ArrayList<WordDto>()
    private var selectedEditId: Int = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.fragmentView = view
        this.wordsRepository = (activity?.application as VocabularyContext).getWordsRepository()
        this.languagesRepository =
            (activity?.application as VocabularyContext).getLanguagesRepository()
        this.subjectsRepository =
            (activity?.application as VocabularyContext).getSubjectsRepository()
        languagesSpinnerAggregator =
            LanguagesSpinnerAggregator(fragmentView.findViewById(R.id.languages_filter))
        this.subjectsSpinnerAggregator =
            SubjectsSpinnerAggregator(fragmentView.findViewById(R.id.subjects_filter))
        wordsTable = view.findViewById(R.id.words_table)
        initRows()
        setupLanguageSpinner()
    }

    override fun onStart() {
        super.onStart()
        requireActivity().findViewById<Button>(R.id.add_word_btn).setOnClickListener { v: View ->
            if (languagesSpinnerAggregator.isFirstItemSelected()) {
                showToast(v.context, "Select language")
            } else if (subjectsSpinnerAggregator.getSelectedItemPosition() == -1) {
                showToast(v.context, "Select subject")
            } else {
                showAddWordPopup(v)
            }
        }
    }

    private fun setupLanguageSpinner() {
        val languages: List<String>
        languagesRepository.fetch().use { cursor ->
            languages = extractElementsFromCursorToArrayList(cursor,
                { ArrayList() },
                { cursor.getString(1) })
        }

        languagesSpinnerAggregator.updateDataAndPutIntoActivity(languages, requireActivity())

        val onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>, view: View?, position: Int, id: Long
            ) {
                //Update list of subjects, if specific language is chosen
                updateSubjectsOnLanguageSelected()
                updateRowsHavingLanguageAndSubjectSelected()
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

            val subjectList: List<String>
            subjectsRepository.fetchForLanguageId(selectedLanguageId).use { cursor ->
                subjectList = extractElementsFromCursorToArrayList(cursor,
                    { ArrayList() },
                    { cursor.getString(1) })
            }

            subjectsSpinnerAggregator.updateDataAndPutIntoActivity(subjectList, requireActivity())

            val onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>, view: View?, position: Int, id: Long
                ) {
                    updateRowsHavingLanguageAndSubjectSelected()
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

    private fun initRows() {
        wordsList = ArrayList()
        val cursor = wordsRepository.fetch()
        cursor.use {
            var index = 0
            while (!cursor.isAfterLast) {
                val word = WordDto(
                    null, cursor.getString(1), cursor.getString(2), cursor.getInt(3)
                )
                insertRow(word, index++)
                wordsList.add(word)
                cursor.moveToNext()
            }
        }
    }

    private fun updateRows() {
        cleanTable()
        wordsList = ArrayList()
        val cursor = wordsRepository.fetch()
        cursor.use {
            var index = 0
            while (!cursor.isAfterLast) {
                val word = WordDto(
                    null, cursor.getString(1), cursor.getString(2), cursor.getInt(3)
                )
                insertRow(word, index++)
                wordsList.add(word)
                cursor.moveToNext()
            }
        }
    }

    private fun updateRowsHavingLanguageAndSubjectSelected() {
        cleanTable()
        wordsList = ArrayList()
        val selectedLanguageId = getLanguageIdFromSelectedInSpinner()
        val selectedSubject = subjectsSpinnerAggregator.getSelectedItemValue()
        if (selectedLanguageId != 1 && selectedSubject != null) {
            wordsRepository.fetchForLanguageAndSubject(selectedLanguageId, selectedSubject)
                .use { cursor ->
                    var index = 0
                    while (!cursor.isAfterLast) {
                        val word = WordDto(
                            null,
                            cursor.getString(1),
                            cursor.getString(2),
                            cursor.getInt(3),
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
            TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT
        )
        val originalParams = TableRow.LayoutParams(
            TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT, 1f
        )

        //Original columns
        val original = AllWordsFragmentHelper.createTextView(
            fragmentView.context, AllWordsFragmentHelper.separateTextIntoRows(wordDto.original)
        )
        original.layoutParams = originalParams
        original.setBackgroundResource(R.drawable.table_item_left_elem)
        original.setPadding(5.dpToPx(fragmentView.context), 0, 0, 0)

        //Translation column
        val translation = AllWordsFragmentHelper.createTextView(
            fragmentView.context, AllWordsFragmentHelper.separateTextIntoRows(wordDto.translate)
        )
        translation.layoutParams = originalParams
        translation.setBackgroundResource(R.drawable.table_item_right_elem)
        translation.setPadding(5.dpToPx(fragmentView.context), 0, 0, 0)

        //Subject column
        tableRow.layoutParams = TableRow.LayoutParams(
            TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT, 0f
        )
        tableRow.setPadding(0, 0, 0, 5)
        tableRow.setOnClickListener { showEditWordPopup(indexNum) }
        tableRow.setBackgroundColor(Color.parseColor("#CCCCCC"))
        tableRow.addView(original)
        tableRow.addView(translation)
        wordsTable.addView(tableRow)
        fragmentView.refreshDrawableState()
    }

    /**
     * Shows popup to word edit and removal
     */
    private fun showEditWordPopup(wordIndex: Int): Boolean {
        if (!this::editWordPopupView.isInitialized) {
            initEditWordPopup()
        }
        val wordDto = wordsList[wordIndex]
        val originalText = wordDto.original
        val translatedText = wordDto.translate

        selectedEditId = wordsRepository.findIdByOriginal(originalText)
        editWordPopupWindow.showAtLocation(view, Gravity.CENTER, 0, 0)

        dimBackground(requireActivity().window, 0.5f) // Add background dim

        editWordPopupView.findViewById<EditText>(R.id.word_original_input_edit)
            .setText(originalText)
        editWordPopupView.findViewById<EditText>(R.id.word_translated_input_edit)
            .setText(translatedText)

        val subjectsFilter = editWordPopupView.findViewById<Spinner>(R.id.word_subject_filter)

        var selectedLanguageId = getLanguageIdFromSelectedInSpinner()

        val selectedSubject: String
        if (selectedLanguageId == 1) {
            val subjectId = wordsRepository.fetchByOriginal(originalText).getInt(3)

            val cursor = subjectsRepository.fetchForSubjectId(subjectId)
            val subjectOriginal = cursor.getString(1)
            selectedSubject = subjectOriginal
            selectedLanguageId = cursor.getInt(2)
        } else {
            selectedSubject = subjectsSpinnerAggregator.getSelectedItemValue()!!
        }

        val subjects = subjectsRepository.fetchForLanguageId(selectedLanguageId).use { cursor ->
            extractElementsFromCursorToArrayList(cursor, { ArrayList() }, { cursor.getString(1) })
        }

        subjectsFilter.adapter = createDefaultDropdownDataAdapter(requireActivity(), subjects)

        subjectsFilter.setSelection(
            AllWordsFragmentHelper.getPositionOfTextInSpinner(
                selectedSubject, subjectsFilter
            )
        )

        return true
    }

    /**
     * Shows popup to new word creation
     */
    private fun showAddWordPopup(view: View) {
        if (!this::addWordPopupView.isInitialized) {
            initAddWordsPopup()
        }
        addWordPopupWindow.showAtLocation(view, Gravity.CENTER, 0, 0)
        dimBackground(requireActivity().window, 0.5f) // Add background dim
        addWordPopupView.findViewById<TextView>(R.id.language_text_value).text =
            languagesSpinnerAggregator.getSelectedItemValue()
        addWordPopupView.findViewById<TextView>(R.id.subject_text_value).text =
            subjectsSpinnerAggregator.getSelectedItemValue()!!
    }


    private fun addWord(view: View) {
        val original = view.findViewById<EditText>(R.id.word_original_input).text.toString()
        val selectedLanguageId = getLanguageIdFromSelectedInSpinner()
        if (original.isNotEmpty()) {
            val translated = view.findViewById<EditText>(R.id.word_translated_input).text.toString()
            val selectedSubject = subjectsSpinnerAggregator.getSelectedItemValue()
            val cursor = subjectsRepository.fetchForSubjectAndLanguageId(
                selectedSubject!!, selectedLanguageId
            )
            val subjectId = cursor.getInt(0)
            cursor.close()
            if (wordsRepository.existsOriginalWithSubjectId(original, subjectId)) {
                showToast(view.context, "Word $original already exists in subject $selectedSubject")
            } else {
                // Save word
                val wordDto = WordDataDto(null, original, translated, subjectId)
                wordsRepository.insert(wordDto)
            }
        }
    }

    private fun updateWord(popupView: View) {
        val newOriginal =
            popupView.findViewById<EditText>(R.id.word_original_input_edit).text.toString()
        if (newOriginal.isNotEmpty()) {
            val translated =
                popupView.findViewById<EditText>(R.id.word_translated_input_edit).text.toString()
            val selectedSubject =
                popupView.findViewById<Spinner>(R.id.word_subject_filter).selectedItem.toString()
            val selectedLanguageId = getLanguageIdFromSelectedInSpinner()
            var cursor = subjectsRepository.fetchForSubjectAndLanguageId(
                selectedSubject, selectedLanguageId
            )
            val subjectId = cursor.getInt(0)
            cursor.close()
            cursor = wordsRepository.findById(selectedEditId)
            val oldOriginal = cursor.getString(1)
            cursor.close()
            if (!oldOriginal.equals(newOriginal) && wordsRepository.existsOriginalWithSubjectId(
                    newOriginal,
                    subjectId
                )
            ) {
                showToast(
                    popupView.context,
                    "Word $newOriginal already exists in subject $selectedSubject"
                )
            } else {
                val wordDataDto =
                    WordDataDto(selectedEditId, newOriginal, translated, subjectId)
                wordsRepository.update(wordDataDto)
            }
        }
    }

    private fun deleteSelectedWord() {
        wordsRepository.delete(selectedEditId)
    }

    private fun cleanTable() {
        val wordsTable = fragmentView.findViewById<TableLayout>(R.id.words_table)
        wordsTable.removeViews(1, wordsTable.size - 1)
    }

    private fun Int.dpToPx(context: Context): Int {
        return (this * context.resources.displayMetrics.density).toInt()
    }

    private fun initAddWordsPopup() {
        val setEditTextsToEmpty = fun() {
            clearEditText(addWordPopupView.findViewById(R.id.word_original_input))
            clearEditText(addWordPopupView.findViewById(R.id.word_translated_input))
        }
        val inflater = LayoutInflater.from(context)
        addWordPopupView = inflater.inflate(R.layout.add_word_popup, null)
        addWordPopupWindow = PopupWindow(
            addWordPopupView,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            true
        )
        addWordPopupView.findViewById<Button>(R.id.add_btn).setOnClickListener {
            addWord(addWordPopupView)
            updateRowsHavingLanguageAndSubjectSelected()
            setEditTextsToEmpty()
            addWordPopupWindow.dismiss()
        }
        addWordPopupView.findViewById<ImageButton>(R.id.close_window_btn).setOnClickListener {
            setEditTextsToEmpty()
            addWordPopupWindow.dismiss()
        }
        addWordPopupWindow.setOnDismissListener {
            dimBackground(requireActivity().window, 0f) // Remove dim when dismissed
        }
    }

    private fun initEditWordPopup() {
        val inflater = LayoutInflater.from(context)
        editWordPopupView = inflater.inflate(R.layout.remove_word_popup, null)

        editWordPopupWindow = PopupWindow(
            editWordPopupView,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            true
        )

        editWordPopupWindow.setOnDismissListener {
            dimBackground(requireActivity().window, 0f) // Remove dim when dismissed
        }

        editWordPopupView.findViewById<Button>(R.id.update_btn).setOnClickListener {
            updateWord(editWordPopupView)
            updateWordsTable()
            editWordPopupWindow.dismiss()
        }
        editWordPopupView.findViewById<Button>(R.id.delete_word_btn).setOnClickListener {
            deleteSelectedWord()
            updateWordsTable()
            editWordPopupWindow.dismiss()
        }
        editWordPopupView.findViewById<ImageButton>(R.id.close_window_btn_edit).setOnClickListener {
            editWordPopupWindow.dismiss()
        }

        initSubjectsSpinnerInEditWordPopup()

    }

    private fun updateWordsTable() {
        if (languagesSpinnerAggregator.isFirstItemSelected()) {
            updateRows()
        } else {
            updateRowsHavingLanguageAndSubjectSelected()
        }
    }

    private fun initSubjectsSpinnerInEditWordPopup() {
        val subjectsFilter = editWordPopupView.findViewById<Spinner>(R.id.word_subject_filter)

        subjectsFilter.isEnabled = true
        subjectsFilter.isVisible = true
    }

    private fun setLanguageForLanguageSpinner(languageId: Int) {
        val language = languagesRepository.fetchLanguageById(languageId).getString(0)
        languagesSpinnerAggregator.selectLanguage(language)
    }

    private fun getLanguageIdFromSelectedInSpinner(): Int {
        val selectedLanguage = languagesSpinnerAggregator.getSelectedItemValue()
        return languagesRepository.fetchId(selectedLanguage!!)
    }
}