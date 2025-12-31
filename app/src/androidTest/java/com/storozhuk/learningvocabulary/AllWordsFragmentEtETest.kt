package com.storozhuk.learningvocabulary

import androidx.fragment.app.testing.FragmentScenario
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.hasChildCount
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withParent
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.storozhuk.learningvocabulary.app.DefaultEtETest
import com.storozhuk.learningvocabulary.dto.data.SubjectDto
import com.storozhuk.learningvocabulary.dto.data.WordDataDto
import com.storozhuk.learningvocabulary.dto.ui.LanguageDto
import com.storozhuk.learningvocabulary.ui.home.AllWordsFragment
import com.storozhuk.learningvocabulary.util.ViewMatcher.withSpinnerContainingText
import com.storozhuk.learningvocabulary.util.ViewMatcherUtil.Companion.withTableHavingRow
import com.storozhuk.learningvocabulary.util.ViewMatcherUtil.Companion.withTableRowHavingValues
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.instanceOf
import org.hamcrest.Matchers.`is`
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AllWordsFragmentEtETest : DefaultEtETest() {
    @get:Rule
    var activityScenarioRule = ActivityScenarioRule(MainActivity::class.java)

    companion object {
        private val DEFAULT_LANGUAGE = "lang1"
        private val DEFAULT_SUBJECT = "subj1"
        private val DEFAULT_WORD_ORIGINAL = "original"
        private val DEFAULT_WORD_TRANSLATE = "translate"
    }

    @Before
    fun init() {
    }

    @Test
    fun shouldNotDisplayAddWordPopup_whenAddWord_givenNoLanguageSelected() {
        //given
        FragmentScenario.launchInContainer(AllWordsFragment::class.java)

        //when
        onView(withId(R.id.add_word_btn)).perform(click())

        //then
        onView(withId(R.id.add_btn)).check(doesNotExist())
    }

    @Test
    fun shouldNotDisplayAddWordPopup_whenAddWord_givenNoSubjectSelected() {
        //given
        insertLanguage(null, DEFAULT_LANGUAGE)
        insertLanguage(null, "lang2")
        FragmentScenario.launchInContainer(AllWordsFragment::class.java)

        //when
        onView(withSpinnerContainingText(DEFAULT_LANGUAGE)).perform(click())
        onView(withText(DEFAULT_LANGUAGE)).perform(click())
        onView(withId(R.id.add_word_btn)).perform(click())

        //then
        onView(withId(R.id.add_btn)).check(doesNotExist())
    }

    @Test
    fun shouldOpenAddWordPopup_whenAddWord_givenLanguageAndSubjectSelected() {
        //given
        insertLanguage(null, DEFAULT_LANGUAGE)
        insertSubject(null, DEFAULT_SUBJECT, 2)
        FragmentScenario.launchInContainer(AllWordsFragment::class.java)

        //when
        onView(withSpinnerContainingText(DEFAULT_LANGUAGE)).perform(click())
        onView(withText(DEFAULT_LANGUAGE)).perform(click())
        onView(withSpinnerContainingText(DEFAULT_SUBJECT)).perform(click())
        onView(withText(DEFAULT_SUBJECT)).perform(click())
        onView(withId(R.id.add_word_btn)).perform(click())

        //then
        onView(withId(R.id.add_btn)).check(matches(isDisplayed()))
    }

    @Test
    fun shouldNotAddWord_whenAddWord_givenNoOriginalInputWritten() {
        //given
        insertLanguage(null, DEFAULT_LANGUAGE)
        insertSubject(null, DEFAULT_SUBJECT, 2)
        FragmentScenario.launchInContainer(AllWordsFragment::class.java)

        //when
        onView(withSpinnerContainingText(DEFAULT_LANGUAGE)).perform(click())
        onView(withText(DEFAULT_LANGUAGE)).perform(click())
        onView(withSpinnerContainingText(DEFAULT_SUBJECT)).perform(click())
        onView(withText(DEFAULT_SUBJECT)).perform(click())
        onView(withId(R.id.add_word_btn)).perform(click())
        onView(withId(R.id.word_original_input)).perform(replaceText(""))
        onView(withId(R.id.add_btn)).perform(click())

        //then
        //Size 1 as the header is included also
        onView(withId(R.id.words_table)).check(matches(hasChildCount(1)))
    }

    @Test
    fun shouldAddWord_whenAddWord_givenOriginalAndTranslationWritten() {
        //given
        val original = DEFAULT_WORD_ORIGINAL
        val translation = DEFAULT_WORD_TRANSLATE

        insertLanguage(null, DEFAULT_LANGUAGE)
        insertSubject(null, DEFAULT_SUBJECT, 2)
        FragmentScenario.launchInContainer(AllWordsFragment::class.java)

        //when
        onView(withSpinnerContainingText(DEFAULT_LANGUAGE)).perform(click())
        onView(withText(DEFAULT_LANGUAGE)).perform(click())
        onView(withSpinnerContainingText(DEFAULT_SUBJECT)).perform(click())
        onView(withText(DEFAULT_SUBJECT)).perform(click())
        onView(withId(R.id.add_word_btn)).perform(click())
        onView(withId(R.id.word_original_input)).perform(replaceText(original))
        onView(withId(R.id.word_translated_input)).perform(replaceText(translation))
        onView(withId(R.id.add_btn)).perform(click())

        //then
        onView(withId(R.id.words_table)).check(matches(hasChildCount(2)))
        onView(withId(R.id.words_table)).check(matches(withTableHavingRow(original, translation)))
    }

    @Test
    fun shouldNotAddWord_whenAddWord_givenDuplicatedOriginalValue() {
        //given
        val original = DEFAULT_WORD_ORIGINAL
        val translation = "translation2"

        insertLanguage(null, DEFAULT_LANGUAGE)
        insertSubject(null, DEFAULT_SUBJECT, 2)
        insertWord(null, DEFAULT_WORD_ORIGINAL, DEFAULT_WORD_TRANSLATE, 1)
        FragmentScenario.launchInContainer(AllWordsFragment::class.java)

        //when
        onView(withSpinnerContainingText(DEFAULT_LANGUAGE)).perform(click())
        onView(withText(DEFAULT_LANGUAGE)).perform(click())
        onView(withSpinnerContainingText(DEFAULT_SUBJECT)).perform(click())
        onView(withText(DEFAULT_SUBJECT)).perform(click())
        onView(withId(R.id.add_word_btn)).perform(click())
        onView(withId(R.id.word_original_input)).perform(replaceText(original))
        onView(withId(R.id.word_translated_input)).perform(replaceText(translation))
        onView(withId(R.id.add_btn)).perform(click())

        //then
        onView(withId(R.id.words_table)).check(matches(hasChildCount(2)))

        onView(withId(R.id.words_table)).check(
            matches(
                withTableHavingRow(
                    DEFAULT_WORD_ORIGINAL, DEFAULT_WORD_TRANSLATE
                )
            )
        )
        onView(
            allOf(
                withParent(withId(R.id.words_table)), withTableHavingRow(original, translation)
            )
        ).check(
            doesNotExist()
        )
    }

    @Test
    fun shouldDisplayUpdateWordPopup_whenUpdateWord_givenClickOnWord() {
        //given
        val original = DEFAULT_WORD_ORIGINAL
        val translation = DEFAULT_WORD_TRANSLATE

        insertLanguage(null, DEFAULT_LANGUAGE)
        insertSubject(1, DEFAULT_SUBJECT, 2)
        insertSubject(2, "test", 2)
        insertWord(null, original, translation, 1)
        FragmentScenario.launchInContainer(AllWordsFragment::class.java)

        //when
        onView(withSpinnerContainingText(DEFAULT_LANGUAGE)).perform(click())
        onView(withText(DEFAULT_LANGUAGE)).perform(click())
        onView(withSpinnerContainingText(DEFAULT_SUBJECT)).perform(click())
        onView(withText(DEFAULT_SUBJECT)).perform(click())
        onView(
            allOf(
                withParent(withId(R.id.words_table)),
                withTableRowHavingValues(original, translation)
            )
        ).perform(click())

        //then - verify values of popup display
        onView(withId(R.id.word_original_input_edit)).check(matches(withText(original)))
        onView(withId(R.id.word_translated_input_edit)).check(matches(withText(translation)))
        onView(withId(R.id.word_subject_filter)).check(
            matches(
                withSpinnerContainingText(
                    DEFAULT_SUBJECT
                )
            )
        )
    }

    @Test
    fun shouldUpdateWord_whenUpdateWord_givenUpdatedWord() {
        //given
        val original = DEFAULT_WORD_ORIGINAL
        val translation = DEFAULT_WORD_TRANSLATE

        val updatedOriginal = DEFAULT_WORD_ORIGINAL + "upd"
        val updatedTranslation = DEFAULT_WORD_TRANSLATE + "upd"

        insertLanguage(null, DEFAULT_LANGUAGE)
        insertSubject(1, "test", 2)
        insertSubject(2, DEFAULT_SUBJECT, 2)
        insertWord(null, "original2", translation, 2)
        insertWord(null, original, translation, 2)
        FragmentScenario.launchInContainer(AllWordsFragment::class.java)

        //when
        onView(withSpinnerContainingText(DEFAULT_LANGUAGE)).perform(click())
        onView(withText(DEFAULT_LANGUAGE)).perform(click())
        selectSubjectOnMainFragment(DEFAULT_SUBJECT)
        onView(
            allOf(
                withParent(withId(R.id.words_table)),
                withTableRowHavingValues(original, translation)
            )
        ).perform(click())
        onView(withId(R.id.word_original_input_edit)).perform(replaceText(updatedOriginal))
        onView(withId(R.id.word_translated_input_edit)).perform(replaceText(updatedTranslation))
        // Click on the spinner to open dropdown
        selectSubjectOnAddWordPopup(DEFAULT_SUBJECT)

        onView(withId(R.id.update_btn)).perform(click())

        //then - verify values is updated
        onView(withId(R.id.words_table)).check(
            matches(
                withTableHavingRow(
                    updatedOriginal, updatedTranslation
                )
            )
        )
        onView(
            allOf(
                withParent(withId(R.id.words_table)), withTableHavingRow(original, translation)
            )
        ).check(
            doesNotExist()
        )
    }

    @Test
    fun shouldUpdateSubjectForWord_whenUpdateWord_givenUpdatedSubject() {
        //given
        val original = DEFAULT_WORD_ORIGINAL
        val translation = DEFAULT_WORD_TRANSLATE

        val anotherSubject = "another"

        insertLanguage(null, DEFAULT_LANGUAGE)
        insertSubject(1, anotherSubject, 2)
        insertSubject(2, DEFAULT_SUBJECT, 2)
        insertWord(null, "dummy", "dummy", 2)
        insertWord(null, original, translation, 2)
        FragmentScenario.launchInContainer(AllWordsFragment::class.java)

        //when
        onView(withSpinnerContainingText(DEFAULT_LANGUAGE)).perform(click())
        onView(withText(DEFAULT_LANGUAGE)).perform(click())
        selectSubjectOnMainFragment(DEFAULT_SUBJECT)
        onView(
            allOf(
                withParent(withId(R.id.words_table)),
                withTableRowHavingValues(original, translation)
            )
        ).perform(click())
        // Click on the spinner to open dropdown
        selectSubjectOnAddWordPopup(anotherSubject)

        onView(withId(R.id.update_btn)).perform(click())

        //then - verify word is not in current subject
        onView(
            allOf(
                withParent(withId(R.id.words_table)), withTableHavingRow(original, translation)
            )
        ).check(
            doesNotExist()
        )
        //then - verify word is moved to another subject
        selectSubjectOnMainFragment(anotherSubject)
        onView(withId(R.id.words_table)).check(matches(withTableHavingRow(original, translation)))
    }

    @Test
    fun shouldNotUpdateWord_whenUpdateWord_givenEmptyOriginal() {
        //given
        val original = DEFAULT_WORD_ORIGINAL
        val translation = DEFAULT_WORD_TRANSLATE

        insertLanguage(null, DEFAULT_LANGUAGE)
        insertSubject(1, "test", 2)
        insertSubject(2, DEFAULT_SUBJECT, 2)
        insertWord(null, "original2", translation, 2)
        insertWord(null, original, translation, 2)
        FragmentScenario.launchInContainer(AllWordsFragment::class.java)

        //when
        onView(withSpinnerContainingText(DEFAULT_LANGUAGE)).perform(click())
        onView(withText(DEFAULT_LANGUAGE)).perform(click())
        selectSubjectOnMainFragment(DEFAULT_SUBJECT)
        onView(
            allOf(
                withParent(withId(R.id.words_table)),
                withTableRowHavingValues(original, translation)
            )
        ).perform(click())
        onView(withId(R.id.word_original_input_edit)).perform(replaceText(""))
        onView(withId(R.id.word_translated_input_edit)).perform(replaceText(""))
        onView(withId(R.id.update_btn)).perform(click())

        //then - verify values is updated
        onView(withId(R.id.words_table)).check(
            matches(
                withTableHavingRow(
                    original, translation
                )
            )
        )
        onView(
            allOf(
                withParent(withId(R.id.words_table)), withTableHavingRow("", "")
            )
        ).check(
            doesNotExist()
        )
    }

    private fun selectSubjectOnMainFragment(subjectName: String) {
        onView(withId(R.id.subjects_filter)).perform(click())
        // Select item by string value
        onData(allOf(`is`(instanceOf(String::class.java)), `is`(subjectName)))
            .perform(click())
    }

    private fun selectSubjectOnAddWordPopup(subjectName: String) {
        onView(withId(R.id.word_subject_filter)).perform(click())
        // Select item by string value
        onData(allOf(`is`(instanceOf(String::class.java)), `is`(subjectName)))
            .perform(click())
    }

    private fun insertSubject(id: Int?, subjectName: String, languageId: Int) {
        testDbHelper.insertSubject(SubjectDto(id, subjectName, languageId))
    }

    private fun insertLanguage(id: Int?, languageName: String) {
        testDbHelper.insertLanguage(LanguageDto(id, languageName))
    }

    private fun insertWord(id: Int?, original: String, translated: String?, subjectId: Int) {
        testDbHelper.insertWord(WordDataDto(id, original, translated, subjectId))
    }
}