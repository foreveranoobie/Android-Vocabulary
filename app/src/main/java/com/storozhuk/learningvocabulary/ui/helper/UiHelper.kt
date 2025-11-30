package com.storozhuk.learningvocabulary.ui.helper

import android.content.Context
import android.database.Cursor
import android.view.Window
import android.widget.EditText
import android.widget.Toast

class UiHelper {
    companion object {
        private val EMPTY_STRING = ""

        @JvmStatic
        fun dimBackground(activityWindow: Window, dimAmount: Float) {
            val layoutParams = activityWindow.attributes
            layoutParams.alpha = 1f - dimAmount
            activityWindow.attributes = layoutParams
        }

        @JvmStatic
        fun showToast(context: Context, message: String){
            Toast.makeText(
                context,
                message,
                Toast.LENGTH_SHORT
            ).show()
        }

        @JvmStatic
        fun cursorHasString(data: Cursor, value: String) : Boolean {
            while(!data.isAfterLast){
                if(value == data.getString(0)){
                    return true
                }
                data.moveToNext()
            }
            return false;
        }

        @JvmStatic
        fun clearEditText(editText: EditText){
            editText.setText(EMPTY_STRING)
        }
    }
}