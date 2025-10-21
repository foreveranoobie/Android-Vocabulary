package com.storozhuk.learningvocabulary

import androidx.fragment.app.testing.FragmentScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.storozhuk.learningvocabulary.ui.home.AllWordsFragment
import com.storozhuk.learningvocabulary.ui.language.AddLanguageFragment
import com.storozhuk.learningvocabulary.util.ViewMatcher.withRecyclerViewContainingText
import com.storozhuk.learningvocabulary.util.ViewMatcher.withSpinnerContainingText
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class AllWordsFragmentEtETest {
    @get:Rule
    var activityScenarioRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun shouldAddLanguages_whenUsingAddLanguageFragment_givenLanguages(){
        //given
        val languageFirst = "Polish"
        val languageSecond = "English"
        //Launch the fragment
        FragmentScenario.launchInContainer(AddLanguageFragment::class.java)

        //when
        onView(withId(R.id.new_language_field)).perform(typeText(languageFirst))
        onView(withId(R.id.add_language_btn)).perform(click())
        onView(withId(R.id.new_language_field)).perform(typeText(languageSecond))
        onView(withId(R.id.add_language_btn)).perform(click())

        //then
        onView(withId(R.id.languages)).check(ViewAssertions.matches(withRecyclerViewContainingText(languageFirst)))
        onView(withId(R.id.languages)).check(ViewAssertions.matches(withRecyclerViewContainingText(languageSecond)))
    }

    @Test
    fun shouldShowAddedLanguageOnWordsFragment_whenUsingAddLanguageFragment_givenAddedLanguage(){
        //given
        val givenLanguage = "English"
        //Launch the fragment
        FragmentScenario.launchInContainer(AddLanguageFragment::class.java)

        //when
        onView(withId(R.id.new_language_field)).perform(typeText(givenLanguage))
        onView(withId(R.id.add_language_btn)).perform(click())

        FragmentScenario.launchInContainer(AllWordsFragment::class.java)

        //then
        onView(withId(R.id.languages_filter)).check(ViewAssertions.matches(withSpinnerContainingText(givenLanguage)));
    }
}