package com.storozhuk.learningvocabulary

import androidx.fragment.app.testing.FragmentScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.storozhuk.learningvocabulary.app.DefaultEtETest
import com.storozhuk.learningvocabulary.dto.ui.LanguageDto
import com.storozhuk.learningvocabulary.ui.language.AddLanguageFragment
import com.storozhuk.learningvocabulary.util.ViewMatcher
import com.storozhuk.learningvocabulary.util.ViewMatcherUtil.Companion.withRecyclerViewMatchingSize
import org.junit.Rule
import org.junit.Test

class AddLanguageFragmentEtETest : DefaultEtETest() {

    companion object {
        val DEFAULT_LANGUAGE = "lang1"
        val LANGUAGE_LANG2 = "lang2"
    }

    @get:Rule
    var activityScenarioRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun shouldNotAddLanguage_whenAddingLanguage_givenEmptyLanguageInput() {
        //given
        insertLanguage(null, DEFAULT_LANGUAGE)
        insertLanguage(null, LANGUAGE_LANG2)
        FragmentScenario.launchInContainer(AddLanguageFragment::class.java)

        //when
        onView(withId(R.id.add_language_btn)).perform(click())

        //then
        onView(withId(R.id.languages)).check(ViewAssertions.matches(withRecyclerViewMatchingSize(2)))
    }

    @Test
    fun shouldNotAddLanguage_whenAddingLanguage_givenExistingLanguageInput() {
        //given
        insertLanguage(null, DEFAULT_LANGUAGE)
        insertLanguage(null, LANGUAGE_LANG2)
        FragmentScenario.launchInContainer(AddLanguageFragment::class.java)

        //when
        onView(withId(R.id.new_language_field)).perform(replaceText(DEFAULT_LANGUAGE))
        onView(withId(R.id.add_language_btn)).perform(click())

        //then
        onView(withId(R.id.languages)).check(ViewAssertions.matches(withRecyclerViewMatchingSize(2)))
    }

    @Test
    fun shouldAddLanguage_whenAddingLanguage_givenNewLanguageInput() {
        //given
        insertLanguage(null, DEFAULT_LANGUAGE)
        insertLanguage(null, LANGUAGE_LANG2)
        FragmentScenario.launchInContainer(AddLanguageFragment::class.java)
        val givenLanguage = "lang3"

        //when
        onView(withId(R.id.new_language_field)).perform(replaceText(givenLanguage))
        onView(withId(R.id.add_language_btn)).perform(click())

        //then
        onView(withId(R.id.languages)).check(ViewAssertions.matches(withRecyclerViewMatchingSize(3)))
        onView(withId(R.id.languages)).check(
            ViewAssertions.matches(
                ViewMatcher.withRecyclerViewContainingText(
                    givenLanguage
                )
            )
        )
    }

    @Test
    fun shouldDeleteLanguage_whenAddingLanguage_givenLanguageRemoved() {
        //given
        insertLanguage(null, DEFAULT_LANGUAGE)
        insertLanguage(null, LANGUAGE_LANG2)
        FragmentScenario.launchInContainer(AddLanguageFragment::class.java)

        //when
        onView(withText(DEFAULT_LANGUAGE)).perform(click())
        onView(withId(R.id.remove_lang_btn)).perform(click())

        //then
        onView(withId(R.id.languages)).check(ViewAssertions.matches(withRecyclerViewMatchingSize(1)))
        onView(withText(DEFAULT_LANGUAGE)).check(doesNotExist())
    }

    @Test
    fun shouldUpdateLanguage_whenAddingLanguage_givenLanguageUpdated() {
        //given
        insertLanguage(null, DEFAULT_LANGUAGE)
        insertLanguage(null, LANGUAGE_LANG2)
        FragmentScenario.launchInContainer(AddLanguageFragment::class.java)
        val givenUpdateLanguage = "lang2Upd"

        //when
        onView(withText(DEFAULT_LANGUAGE)).perform(click())
        onView(withId(R.id.edit_language_input)).perform(replaceText(givenUpdateLanguage))
        onView(withId(R.id.edit_lang_btn)).perform(click())

        //then
        onView(withId(R.id.languages)).check(ViewAssertions.matches(withRecyclerViewMatchingSize(2)))
        onView(withText(DEFAULT_LANGUAGE)).check(doesNotExist())
        onView(withId(R.id.languages)).check(
            ViewAssertions.matches(
                ViewMatcher.withRecyclerViewContainingText(
                    givenUpdateLanguage
                )
            )
        )
    }

    private fun insertLanguage(id: Int?, languageName: String) {
        testDbHelper.insertLanguage(LanguageDto(id, languageName))
    }
}