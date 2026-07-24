package com.storozhuk.learningvocabulary.ui.common.spinner

import android.widget.Spinner
import androidx.fragment.app.FragmentActivity
import com.storozhuk.learningvocabulary.ui.home.helper.AllWordsFragmentHelper

class SubjectsSpinnerAggregator(spinner: Spinner) : AbstractSpinnerAggregator(spinner) {
    fun updateAdapter(values: List<String>, activity: FragmentActivity) {
        spinner.adapter =
            AllWordsFragmentHelper.createDefaultDropdownDataAdapter(activity, values)
    }
}