package com.storozhuk.learningvocabulary.ui.home.spinner

import android.widget.AdapterView
import android.widget.Spinner
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import com.storozhuk.learningvocabulary.ui.home.helper.AllWordsFragmentHelper

abstract class AbstractSpinnerAggregator(val spinner: Spinner) {
    fun updateDataAndPutIntoActivity(values: List<String>, activity: FragmentActivity) {
        spinner.adapter =
            AllWordsFragmentHelper.createDefaultDropdownDataAdapter(activity, values)
        spinner.isEnabled = true
        spinner.isVisible = true
    }

    fun setOnItemSelectedListener(listener: AdapterView.OnItemSelectedListener) {
        spinner.onItemSelectedListener = listener
    }

    fun getSelectedItemPosition(): Int = spinner.selectedItemPosition

    fun getSelectedItemValue(): String? =
        if (spinner.selectedItem == null) null else spinner.selectedItem.toString()

    fun isFirstItemSelected(): Boolean = spinner.selectedItemPosition == 0
}