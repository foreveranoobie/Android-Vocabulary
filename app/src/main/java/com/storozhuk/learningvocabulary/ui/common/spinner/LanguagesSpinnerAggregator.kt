package com.storozhuk.learningvocabulary.ui.common.spinner

import android.widget.Spinner
import com.storozhuk.learningvocabulary.ui.home.helper.AllWordsFragmentHelper

class LanguagesSpinnerAggregator(spinner: Spinner) : AbstractSpinnerAggregator(spinner) {

    fun selectLanguage(language: String) {
        val position = AllWordsFragmentHelper.getPositionOfTextInSpinner(language, spinner)
        spinner.setSelection(position)
    }
}