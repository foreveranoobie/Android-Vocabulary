package com.storozhuk.learningvocabulary

import com.google.common.truth.Truth.assertThat
import com.storozhuk.learningvocabulary.ui.home.helper.AllWordsFragmentHelper
import org.junit.Test
import org.junit.runner.RunWith

class AllWordsFragmentHelperUnitTest {
    @Test
    fun `Should create multiline string divided with hyphen given text longer than 12`(){
        //given
        val givenText = "abcdefghijklmnopqrts";
        val expectedText = "abcdefghijkl-\nmnopqrts";

        //when
        val actualText = AllWordsFragmentHelper.separateTextIntoRows(givenText)

        //then
        assertThat(actualText).isEqualTo(expectedText)
    }

    @Test
    fun `Should create string with dots given text longer than 25 symbols`(){
        //given
        val givenText = "abcdefghijklmnopqrtsuwyxz[]123";
        val expectedText = "abcdefghijkl-\nmnopqrtsuwy...";

        //when
        val actualText = AllWordsFragmentHelper.separateTextIntoRows(givenText)

        //then
        assertThat(actualText).isEqualTo(expectedText)
    }
}