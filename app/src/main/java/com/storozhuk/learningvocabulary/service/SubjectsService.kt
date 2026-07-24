package com.storozhuk.learningvocabulary.service

import android.database.Cursor
import com.storozhuk.learningvocabulary.db.repo.SubjectsRepository
import com.storozhuk.learningvocabulary.dto.data.SubjectDataDto

class SubjectsService(val subjectsRepository: SubjectsRepository) {
    fun getSubjectByNameAndLanguageId(subjectName: String, languageId: Int): SubjectDataDto? {
        var cursor: Cursor? = null
        try {
            cursor = subjectsRepository.fetchForSubjectAndLanguageId(subjectName, languageId)
            return if (cursor.count > 0) {
                SubjectDataDto(
                    cursor.getInt(0), cursor.getString(1),
                    cursor.getInt(2)
                )
            } else {
                null
            }
        } catch (_: Exception) {
        } finally {
            cursor?.close()
        }
        return null
    }

    fun getSubjectById(subjectId: Int): SubjectDataDto? {
        var cursor: Cursor? = null
        try {
            cursor = subjectsRepository.fetchForSubjectId(subjectId)
            return if (cursor.count > 0) {
                SubjectDataDto(
                    cursor.getInt(0), cursor.getString(1),
                    cursor.getInt(2)
                )
            } else {
                null
            }
        } catch (_: Exception) {
        } finally {
            cursor?.close()
        }
        return null
    }

    fun getSubjectsForLanguage(languageId: Int): List<SubjectDataDto> {
        var cursor: Cursor? = null
        val result = ArrayList<SubjectDataDto>()
        try {
            cursor = subjectsRepository.fetchForLanguageId(languageId)
            while (!cursor.isAfterLast) {
                result.add(SubjectDataDto(cursor.getInt(0), cursor.getString(1), languageId))
                cursor.moveToNext()
            }
        } catch (_: Exception) {
        } finally {
            cursor?.close()
        }
        return result
    }
}