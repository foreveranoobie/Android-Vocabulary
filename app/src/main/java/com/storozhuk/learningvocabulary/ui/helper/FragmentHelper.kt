package com.storozhuk.learningvocabulary.ui.helper

import android.widget.ArrayAdapter
import androidx.fragment.app.FragmentActivity

class FragmentHelper {
    companion object {
        fun <T> createDefaultDropdownDataAdapter(
            activity: FragmentActivity,
            data: List<T>
        ): ArrayAdapter<T> {
            val dataAdapter =
                ArrayAdapter(activity, android.R.layout.simple_spinner_item, data)
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            return dataAdapter
        }
    }
}