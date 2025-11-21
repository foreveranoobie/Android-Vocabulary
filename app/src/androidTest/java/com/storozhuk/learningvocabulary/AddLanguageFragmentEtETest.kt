package com.storozhuk.learningvocabulary

import androidx.fragment.app.testing.FragmentScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.storozhuk.learningvocabulary.ui.language.AddLanguageFragment
import com.storozhuk.learningvocabulary.ui.subject.AddSubjectFragment
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AddLanguageFragmentEtETest {

    val DEFAULT_SUBJECT = "subject"

    @get:Rule
    var activityScenarioRule = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun init(){
        //Launch the fragment
        FragmentScenario.launchInContainer(AddSubjectFragment::class.java)
    }

    @Test
    fun shouldNotAddSubject_whenAddSubject_givenNoLanguageSelected(){
        //when
        onView(withId(R.id.new_subject_field)).perform(typeText(DEFAULT_SUBJECT))
        onView(withId(R.id.add_subject_btn)).perform(click())

        //then
        onView(withId(R.id.subjects)).noActivity()
    }
}