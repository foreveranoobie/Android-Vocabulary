package com.storozhuk.learningvocabulary.util

import android.view.View
import android.widget.AdapterView
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.matcher.BoundedMatcher
import org.hamcrest.Description

class ViewMatcherUtil {
    companion object {

        @JvmStatic
        fun withRecyclerViewMatchingSize(size: Int): BoundedMatcher<View?, RecyclerView> {
            return object : BoundedMatcher<View?, RecyclerView>(RecyclerView::class.java) {
                override fun matchesSafely(recyclerView: RecyclerView): Boolean {
                    return size == recyclerView.adapter?.itemCount
                }

                override fun describeTo(description: Description) {
                    description.appendText("RecyclerView having size: $size")
                }
            }
        }

        @JvmStatic
        fun withTableMatchingRowsCount(count: Int): BoundedMatcher<View?, TableLayout> {
            return object : BoundedMatcher<View?, TableLayout>(TableLayout::class.java) {
                override fun matchesSafely(item: TableLayout?): Boolean {
                    return count == item?.childCount
                }

                override fun describeTo(description: Description?) {
                    description?.appendText("Table having rows: $count")
                }
            }
        }

        @JvmStatic
        fun withSpinnerContainingText(text: String): BoundedMatcher<View?, AdapterView<*>> {
            return object : BoundedMatcher<View?, AdapterView<*>>(AdapterView::class.java) {
                override fun describeTo(description: Description) {
                    description.appendText("With spinner text containing: $text")
                }

                override fun matchesSafely(view: AdapterView<*>?): Boolean {
                    val adapter = view?.adapter ?: return false
                    for (i in 0 until adapter.count) {
                        if (text == adapter.getItem(i)) {
                            return true
                        }
                    }
                    return false
                }
            }
        }

        fun withTableHavingRow(vararg values: String): BoundedMatcher<View?, TableLayout> {
            return object : BoundedMatcher<View?, TableLayout>(TableLayout::class.java) {
                override fun describeTo(description: Description?) {
                    description?.appendText("With row having values: $values")
                }

                override fun matchesSafely(item: TableLayout?): Boolean {
                    if (item != null) {
                        for (row in item.children) {
                            if ((row as TableRow).childCount != values.size) {
                                return false
                            } else {
                                val tableRow = row as TableRow
                                var equalsCount = 0
                                for (i in 0 until tableRow.childCount) {
                                    if ((tableRow.getChildAt(i) as TextView).text == values[i]) {
                                        equalsCount++
                                    }
                                }
                                if (equalsCount == values.size) {
                                    return true
                                }
                            }
                        }
                        return false
                    } else {
                        return false
                    }
                }

            }
        }

        fun withTableRowHavingValues(vararg values: String): BoundedMatcher<View?, TableRow> {
            return object : BoundedMatcher<View?, TableRow>(TableRow::class.java) {
                override fun describeTo(description: Description?) {
                    description?.appendText("With row having values: $values")
                }

                override fun matchesSafely(item: TableRow?): Boolean {
                    if (item != null) {
                        if (item.childCount != values.size) {
                            return false
                        } else {
                            var equalsCount = 0
                            for (i in 0 until item.childCount) {
                                if ((item.getChildAt(i) as TextView).text == values[i]) {
                                    equalsCount++
                                }
                            }
                            if (equalsCount == values.size) {
                                return true
                            }
                        }
                        return false
                    } else {
                        return false
                    }
                }

            }
        }
    }
}