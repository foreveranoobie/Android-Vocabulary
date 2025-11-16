package com.storozhuk.learningvocabulary.ui.home.helper

import android.content.Context
import android.database.Cursor
import android.graphics.Color
import android.view.View
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TableRow
import android.widget.TextView
import androidx.annotation.VisibleForTesting
import androidx.fragment.app.FragmentActivity
import com.storozhuk.learningvocabulary.R
import java.lang.StringBuilder
import java.util.function.Function
import java.util.function.Supplier

class AllWordsFragmentHelper {
    companion object {
        fun createTextView(context: Context, textToAdd: String?): TextView {
            return TextView(context).apply {
                text = textToAdd
                // Use a visible color like black
                textSize = 18f
                setTextColor(Color.BLACK) // Import android.graphics.Color
            }
        }

        fun separateTextIntoRows(text: String?): String? {
            if (text != null) {
                val copy = StringBuilder()
                if (text.length > 12) {
                    copy.append("${text.substring(0, 12)}-\n${text.substring(12)}")
                    if (text.length > 25) {
                        copy.deleteRange(25, copy.length)
                        copy.append("...")
                    }
                } else {
                    copy.append(text)
                }
                return copy.toString()
            }
            return null
        }

        fun <T> createDefaultDropdownDataAdapter(activity: FragmentActivity, data: List<T>): ArrayAdapter<T>{
            val dataAdapter =
                ArrayAdapter(activity, android.R.layout.simple_spinner_item, data)
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            return dataAdapter
        }

        fun <R> extractElementsFromCursorToArrayList(cursor: Cursor, collectionCreationSupplier: Supplier<ArrayList<R>>,
                                                     dataObjectCreateFunction: Function<Cursor, R>) : ArrayList<R>{
            val data = collectionCreationSupplier.get()
            while (!cursor.isAfterLast) {
                data.add(dataObjectCreateFunction.apply(cursor))
                cursor.moveToNext()
            }
            return data
        }
    }
}