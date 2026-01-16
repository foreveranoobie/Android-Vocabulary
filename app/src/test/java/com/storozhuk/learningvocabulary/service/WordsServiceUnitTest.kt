package com.storozhuk.learningvocabulary.service

import com.storozhuk.learningvocabulary.DataProcessException
import com.storozhuk.learningvocabulary.db.repo.WordsRepository
import com.storozhuk.learningvocabulary.dto.data.WordDataDto
import com.storozhuk.learningvocabulary.dto.ui.WordDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyString
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.refEq

@RunWith(MockitoJUnitRunner::class)
class WordsServiceUnitTest {

    companion object {
        private val DEFAULT_ID = 123
        private val DEFAULT_ORIGINAL = "original"
        private val DEFAULT_TRANSLATE = "translate"
        private val DEFAULT_SUBJECT_ID = 456
    }

    @Mock
    private lateinit var wordsRepositoryMock: WordsRepository;

    @InjectMocks
    private lateinit var service: WordsService

    @Test
    fun shouldCallRepositoryInsert_whenAddWord_givenWordDto() {
        //given
        val givenWordDto =
            WordDto(DEFAULT_ID, DEFAULT_ORIGINAL, DEFAULT_TRANSLATE, DEFAULT_SUBJECT_ID)

        `when`(wordsRepositoryMock.existsOriginalWithSubjectId(anyString(), anyInt()))
            .thenReturn(false)

        val expectedWordDataDto =
            WordDataDto(null, DEFAULT_ORIGINAL, DEFAULT_TRANSLATE, DEFAULT_SUBJECT_ID)

        `when`(wordsRepositoryMock.insert(any())).thenReturn(0L)

        //when
        service.addWord(givenWordDto)

        //then
        verify(wordsRepositoryMock).insert(refEq(expectedWordDataDto))
    }

    @Test
    fun shouldThrowDataProcessExceptionWithCorrectMessage_whenAddWord_givenEmptyOriginal() {
        //given
        val givenWordDto =
            WordDto(DEFAULT_ID, "", DEFAULT_TRANSLATE, DEFAULT_SUBJECT_ID)

        //when
        val exception = assertThrows(DataProcessException::class.java) {
            service.addWord(givenWordDto)
        }

        //then
        assertEquals("Original should not be empty", exception.message)
    }

    @Test
    fun shouldThrowDataProcessExceptionWithCorrectMessage_whenAddWord_givenExistingWord() {
        //given
        val givenWordDto =
            WordDto(DEFAULT_ID, DEFAULT_ORIGINAL, DEFAULT_TRANSLATE, DEFAULT_SUBJECT_ID)

        `when`(wordsRepositoryMock.existsOriginalWithSubjectId(anyString(), anyInt()))
            .thenReturn(true)

        val expectedMessage = "Word $DEFAULT_ORIGINAL already exists in subject $DEFAULT_SUBJECT_ID"

        //when
        val exception = assertThrows(DataProcessException::class.java) {
            service.addWord(givenWordDto)
        }

        //then
        assertEquals(expectedMessage, exception.message)
    }

}