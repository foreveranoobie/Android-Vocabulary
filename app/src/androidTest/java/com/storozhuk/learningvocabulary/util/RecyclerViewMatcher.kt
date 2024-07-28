package com.storozhuk.learningvocabulary.util

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.matcher.BoundedMatcher
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.`is`


object RecyclerViewMatcher {
    fun withViewContainingText(text: String): BoundedMatcher<View?, RecyclerView> {
        return object : BoundedMatcher<View?, RecyclerView>(RecyclerView::class.java) {
            override fun matchesSafely(recyclerView: RecyclerView): Boolean {
                for (i in 0 until recyclerView.childCount) {
                    val child = recyclerView.getChildAt(i)
                    if (child is ViewGroup) {
                        val textView = findTextView(child)
                        if (textView != null && textView.getText().toString() == text) {
                            return true
                        }
                    }
                }
                return false
            }

            override fun describeTo(description: Description) {
                description.appendText("RecyclerView with text: $text")
            }

            private fun findTextView(viewGroup: ViewGroup): TextView? {
                for (i in 0 until viewGroup.childCount) {
                    val child = viewGroup.getChildAt(i)
                    if (child is TextView) {
                        return child
                    } else if (child is ViewGroup) {
                        val textView = findTextView(child)
                        if (textView != null) {
                            return textView
                        }
                    }
                }
                return null
            }
        }
    }
}