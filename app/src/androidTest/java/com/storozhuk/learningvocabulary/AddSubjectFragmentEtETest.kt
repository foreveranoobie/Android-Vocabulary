package com.storozhuk.learningvocabulary

import android.view.View
import androidx.fragment.app.testing.FragmentScenario
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.clearText
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.storozhuk.learningvocabulary.app.DefaultEtETest
import com.storozhuk.learningvocabulary.dto.data.SubjectDto
import com.storozhuk.learningvocabulary.dto.ui.LanguageDto
import com.storozhuk.learningvocabulary.ui.subject.AddSubjectFragment
import com.storozhuk.learningvocabulary.util.ViewMatcher
import com.storozhuk.learningvocabulary.util.ViewMatcherUtil.Companion.withRecyclerViewMatchingSize
import org.hamcrest.Description
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.instanceOf
import org.hamcrest.Matchers.`is`
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AddSubjectFragmentEtETest : DefaultEtETest() {
    companion object {
        const val DEFAULT_SUBJECT = "subject"
        const val SUBJECT_SUBJ1 = "subj1"
    }

    @get:Rule
    var activityScenarioRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun shouldNotAddSubject_whenAddSubject_givenNoLanguageSelected() {
        //when
        FragmentScenario.launchInContainer(AddSubjectFragment::class.java)
        onView(withId(R.id.new_subject_field)).perform(typeText(DEFAULT_SUBJECT))
        onView(withId(R.id.add_subject_btn)).perform(click())

        //then
        onView(withId(R.id.subjects)).check(matches(withRecyclerViewMatchingSize(0)))
    }

    @Test
    fun shouldAddSubject_whenAddSubject_givenLanguageSelected() {
        //given
        insertLanguage(null, "lang1")
        FragmentScenario.launchInContainer(AddSubjectFragment::class.java)
        onView(withId(R.id.subjects_languages_filter)).perform(click())
        onData(
            allOf(
                `is`(instanceOf(String::class.java)), `is`("lang1")
            )
        ).perform(click())

        //when
        onView(withId(R.id.new_subject_field)).perform(typeText(DEFAULT_SUBJECT))
        onView(withId(R.id.add_subject_btn)).perform(click())

        //then
        onView(withId(R.id.subjects)).check(matches(withRecyclerViewMatchingSize(1)))
        onView(withId(R.id.subjects)).check(
            matches(
                ViewMatcher.withRecyclerViewContainingText(
                    DEFAULT_SUBJECT
                )
            )
        )
    }

    @Test
    fun shouldNotAddSubject_whenAddSubject_givenSubjectAlreadyExists() {
        //given
        val subjectName = SUBJECT_SUBJ1
        insertLanguage(2, "lang1")
        insertSubject(null, subjectName, 2)
        insertSubject(null, DEFAULT_SUBJECT, 2)

        FragmentScenario.launchInContainer(AddSubjectFragment::class.java)
        onView(withId(R.id.subjects_languages_filter)).perform(click())
        onData(
            allOf(
                `is`(instanceOf(String::class.java)), `is`("lang1")
            )
        ).perform(click())

        //when
        onView(withId(R.id.new_subject_field)).perform(typeText(subjectName))
        onView(withId(R.id.add_subject_btn)).perform(click())

        //then
        onView(withId(R.id.subjects)).check(matches(withRecyclerViewMatchingSize(2)))
        onView(withId(R.id.subjects)).check(
            matches(
                ViewMatcher.withRecyclerViewContainingText(
                    subjectName
                )
            )
        )
    }

    @Test
    fun shouldDeleteSubject_whenDeleteSubject_givenExistingSubjectDeleted() {
        //given
        val subjectName = SUBJECT_SUBJ1
        val toRemoveSubject = DEFAULT_SUBJECT
        insertLanguage(2, "lang1")
        insertSubject(null, subjectName, 2)
        insertSubject(null, toRemoveSubject, 2)

        FragmentScenario.launchInContainer(AddSubjectFragment::class.java)
        onView(withId(R.id.subjects_languages_filter)).perform(click())
        onData(
            allOf(
                `is`(instanceOf(String::class.java)), `is`("lang1")
            )
        ).perform(click())

        //when
        onView(withText(toRemoveSubject)).perform(click())
        onView(withId(R.id.remove_subject_btn)).perform(click())

        //then
        onView(withId(R.id.subjects)).check(matches(withRecyclerViewMatchingSize(1)))
        onView(withId(R.id.subjects)).check(
            matches(
                ViewMatcher.withRecyclerViewContainingText(
                    subjectName
                )
            )
        )
    }

    @Test
    fun shouldUpdateSubject_whenUpdateSubject_givenNewSubjectName() {
        //given
        val subjectName = SUBJECT_SUBJ1
        val toUpdateSubject = DEFAULT_SUBJECT
        val expectedSubject = "subj2"
        insertLanguage(2, "lang1")
        insertSubject(null, subjectName, 2)
        insertSubject(null, toUpdateSubject, 2)

        FragmentScenario.launchInContainer(AddSubjectFragment::class.java)
        onView(withId(R.id.subjects_languages_filter)).perform(click())
        onData(
            allOf(
                `is`(instanceOf(String::class.java)), `is`("lang1")
            )
        ).perform(click())

        //when
        onView(withText(toUpdateSubject)).perform(click())
        onView(withId(R.id.edit_subject_input)).perform(replaceText(expectedSubject))
        onView(withId(R.id.edit_subject_btn)).perform(click())

        //then
        onView(withId(R.id.subjects)).check(matches(withRecyclerViewMatchingSize(2)))
        onView(withId(R.id.subjects)).check(
            matches(
                ViewMatcher.withRecyclerViewContainingText(
                    expectedSubject
                )
            )
        )
        onView(withText(toUpdateSubject)).check(doesNotExist())
    }

    @Test
    fun shouldNotUpdateSubject_whenUpdateSubject_givenEmptySubjectName() {
        //given
        val subjectName = SUBJECT_SUBJ1
        val expectedSubject = DEFAULT_SUBJECT
        insertLanguage(2, "lang1")
        insertSubject(null, subjectName, 2)
        insertSubject(null, expectedSubject, 2)

        FragmentScenario.launchInContainer(AddSubjectFragment::class.java)
        onView(withId(R.id.subjects_languages_filter)).perform(click())
        onData(
            allOf(
                `is`(instanceOf(String::class.java)), `is`("lang1")
            )
        ).perform(click())

        //when
        onView(withText(expectedSubject)).perform(click())
        onView(withId(R.id.edit_subject_input)).perform(clearText())
        onView(withId(R.id.edit_subject_btn)).perform(click())

        //then
        onView(withId(R.id.subjects)).check(matches(withRecyclerViewMatchingSize(2)))
        onView(withId(R.id.subjects)).check(
            matches(
                ViewMatcher.withRecyclerViewContainingText(
                    expectedSubject
                )
            )
        )
    }

    @Test
    fun shouldNotUpdateSubject_whenUpdateSubject_givenSubjectAlreadyExists() {
        //given
        val firstSubject = SUBJECT_SUBJ1
        val secondSubject = DEFAULT_SUBJECT
        insertLanguage(2, "lang1")
        insertSubject(null, firstSubject, 2)
        insertSubject(null, secondSubject, 2)

        FragmentScenario.launchInContainer(AddSubjectFragment::class.java)
        onView(withId(R.id.subjects_languages_filter)).perform(click())
        onData(
            allOf(
                `is`(instanceOf(String::class.java)), `is`("lang1")
            )
        ).perform(click())

        //when
        onView(withText(secondSubject)).perform(click())
        onView(withId(R.id.edit_subject_input)).perform(replaceText(firstSubject))
        onView(withId(R.id.edit_subject_btn)).perform(click())

        //then
        onView(withId(R.id.subjects)).check(matches(withRecyclerViewMatchingSize(2)))
        onView(withId(R.id.subjects)).check(
            matches(
                ViewMatcher.withRecyclerViewContainingText(
                    firstSubject
                )
            )
        )
        onView(withId(R.id.subjects)).check(
            matches(
                ViewMatcher.withRecyclerViewContainingText(
                    secondSubject
                )
            )
        )
    }

    private fun insertSubject(id: Int?, subjectName: String, languageId: Int) {
        testDbHelper.insertSubject(SubjectDto(id, subjectName, languageId))
    }

    private fun insertLanguage(id: Int?, languageName: String) {
        testDbHelper.insertLanguage(LanguageDto(id, languageName))
    }
}