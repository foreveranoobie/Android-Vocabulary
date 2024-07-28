package com.storozhuk.learningvocabulary.ui.home.helper

import android.content.Context
import android.graphics.Color
import android.view.View
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TableRow
import android.widget.TextView
import androidx.annotation.VisibleForTesting
import com.storozhuk.learningvocabulary.R
import java.lang.StringBuilder

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

        fun createLinearLayout(context: Context, textViewToAdd: TextView): LinearLayout {
            return LinearLayout(context).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = TableRow.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                addView(textViewToAdd)
            }
        }

        fun getSpinnerSelectedValue(spinnerId: Int, view: View): String {
            return view.findViewById<Spinner>(spinnerId).selectedItem.toString()
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
    }
}