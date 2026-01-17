package com.storozhuk.learningvocabulary.service

import android.database.Cursor
import com.storozhuk.learningvocabulary.db.repo.SubjectsRepository
import com.storozhuk.learningvocabulary.dto.data.SubjectDataDto
import org.assertj.core.api.Assertions.assertThat
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyString
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class SubjectsServiceUnitTest {

    companion object {
        private val DEFAULT_ID = 789
        private val DEFAULT_SUBJECT_NAME = "Math"
        private val DEFAULT_LANGUAGE_ID = 1
    }

    @Mock
    private lateinit var subjectsRepositoryMock: SubjectsRepository

    @InjectMocks
    private lateinit var service: SubjectsService

    @Test
    fun shouldReturnSubjectDataDto_whenGetSubjectByNameAndLanguageId_givenExistingSubject() {
        //given
        val cursorMock = mock(Cursor::class.java)
        `when`(cursorMock.count).thenReturn(1)
        `when`(cursorMock.getInt(0)).thenReturn(DEFAULT_ID)
        `when`(cursorMock.getString(1)).thenReturn(DEFAULT_SUBJECT_NAME)
        `when`(cursorMock.getInt(2)).thenReturn(DEFAULT_LANGUAGE_ID)

        `when`(subjectsRepositoryMock.fetchForSubjectAndLanguageId(anyString(), anyInt()))
            .thenReturn(cursorMock)

        val expectedSubjectDataDto =
            SubjectDataDto(DEFAULT_ID, DEFAULT_SUBJECT_NAME, DEFAULT_LANGUAGE_ID)

        //when
        val actualSubjectDataDto =
            service.getSubjectByNameAndLanguageId(DEFAULT_SUBJECT_NAME, DEFAULT_LANGUAGE_ID)

        //then
        assertThat(actualSubjectDataDto).usingRecursiveComparison()
            .isEqualTo(expectedSubjectDataDto)
        verify(cursorMock).close()
    }

    @Test
    fun shouldReturnNull_whenGetSubjectByNameAndLanguageId_givenEmptyCursor() {
        //given
        val cursorMock = mock(Cursor::class.java)
        `when`(cursorMock.count).thenReturn(0)

        `when`(subjectsRepositoryMock.fetchForSubjectAndLanguageId(anyString(), anyInt()))
            .thenReturn(cursorMock)

        //when
        val actualSubjectDataDto =
            service.getSubjectByNameAndLanguageId(DEFAULT_SUBJECT_NAME, DEFAULT_LANGUAGE_ID)

        //then
        assertNull(actualSubjectDataDto)
        verify(cursorMock).close()
    }

    @Test
    fun shouldReturnNull_whenGetSubjectByNameAndLanguageId_givenExceptionThrown() {
        //given
        `when`(subjectsRepositoryMock.fetchForSubjectAndLanguageId(anyString(), anyInt()))
            .thenThrow(RuntimeException("Database error"))

        //when
        val actualSubjectDataDto =
            service.getSubjectByNameAndLanguageId(DEFAULT_SUBJECT_NAME, DEFAULT_LANGUAGE_ID)

        //then
        assertNull(actualSubjectDataDto)
    }

    @Test
    fun shouldReturnSubjectDataDto_whenGetSubjectById_givenExistingSubject() {
        //given
        val cursorMock = mock(Cursor::class.java)
        `when`(cursorMock.count).thenReturn(1)
        `when`(cursorMock.getInt(0)).thenReturn(DEFAULT_ID)
        `when`(cursorMock.getString(1)).thenReturn(DEFAULT_SUBJECT_NAME)
        `when`(cursorMock.getInt(2)).thenReturn(DEFAULT_LANGUAGE_ID)

        `when`(subjectsRepositoryMock.fetchForSubjectId(anyInt()))
            .thenReturn(cursorMock)

        val expectedSubjectDataDto =
            SubjectDataDto(DEFAULT_ID, DEFAULT_SUBJECT_NAME, DEFAULT_LANGUAGE_ID)

        //when
        val actualSubjectDataDto = service.getSubjectById(DEFAULT_ID)

        //then
        assertThat(actualSubjectDataDto).usingRecursiveComparison()
            .isEqualTo(expectedSubjectDataDto)
        verify(cursorMock).close()
    }

    @Test
    fun shouldReturnNull_whenGetSubjectById_givenEmptyCursor() {
        //given
        val cursorMock = mock(Cursor::class.java)
        `when`(cursorMock.count).thenReturn(0)

        `when`(subjectsRepositoryMock.fetchForSubjectId(anyInt()))
            .thenReturn(cursorMock)

        //when
        val actualSubjectDataDto = service.getSubjectById(DEFAULT_ID)

        //then
        assertThat(actualSubjectDataDto).isNull()
        verify(cursorMock).close()
    }

    @Test
    fun shouldReturnNull_whenGetSubjectById_givenExceptionThrown() {
        //given
        `when`(subjectsRepositoryMock.fetchForSubjectId(anyInt()))
            .thenThrow(RuntimeException("Database error"))

        //when
        val actualSubjectDataDto = service.getSubjectById(DEFAULT_ID)

        //then
        assertThat(actualSubjectDataDto).isNull()
    }

    @Test
    fun shouldReturnListOfSubjects_whenGetSubjectsForLanguage_givenCursorWithRows() {
        //given
        val subjectFirst = "sub1"
        val subjectSecond = "sub2"
        val cursorMock = mock(Cursor::class.java)
        `when`(cursorMock.isAfterLast).thenReturn(false, false, true)
        `when`(cursorMock.getString(1)).thenReturn(subjectFirst, subjectSecond)
        `when`(cursorMock.moveToNext()).thenReturn(true, false)

        `when`(subjectsRepositoryMock.fetchForLanguageId(anyInt()))
            .thenReturn(cursorMock)

        //when
        val actualSubjects = service.getSubjectsForLanguage(DEFAULT_LANGUAGE_ID)

        //then
        assertThat(actualSubjects).containsExactly(subjectFirst, subjectSecond)
        verify(cursorMock).close()
    }

    @Test
    fun shouldReturnEmptyList_whenGetSubjectsForLanguage_givenEmptyCursor() {
        //given
        val cursorMock = mock(Cursor::class.java)
        `when`(cursorMock.isAfterLast).thenReturn(true)

        `when`(subjectsRepositoryMock.fetchForLanguageId(anyInt()))
            .thenReturn(cursorMock)

        //when
        val actualSubjects = service.getSubjectsForLanguage(DEFAULT_LANGUAGE_ID)

        //then
        assertThat(actualSubjects).isEmpty()
        verify(cursorMock).close()
    }

    @Test
    fun shouldReturnEmptyList_whenGetSubjectsForLanguage_givenExceptionThrown() {
        //given
        `when`(subjectsRepositoryMock.fetchForLanguageId(anyInt()))
            .thenThrow(RuntimeException("Database error"))

        //when
        val actualSubjects = service.getSubjectsForLanguage(DEFAULT_LANGUAGE_ID)

        //then
        assertThat(actualSubjects).isEmpty()
    }
}