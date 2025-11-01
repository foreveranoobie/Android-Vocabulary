package com.storozhuk.learningvocabulary.ui.helper

import android.database.Cursor
import android.view.Window

class UiHelper {
    companion object {
        @JvmStatic
        fun dimBackground(activityWindow: Window, dimAmount: Float) {
            val layoutParams = activityWindow.attributes
            layoutParams.alpha = 1f - dimAmount
            activityWindow.attributes = layoutParams
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
    }
}