package com.storozhuk.learningvocabulary.db.repo

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.storozhuk.learningvocabulary.db.helper.DatabaseHelper
import com.storozhuk.learningvocabulary.dto.data.SubjectDto

class SubjectsRepository(
    private val database: SQLiteDatabase,
    private val dbHelper: DatabaseHelper
) {

    private val WHERE_ID = "${dbHelper.SUBJECTS_ID_COLUMN}=?"

    private val WHERE_LANGUAGE_ID = "${dbHelper.SUBJECTS_LANGUAGE_ID_COLUMN}=?"

    private val WHERE_SUBJECT_AND_LANGUAGE_ID = "${dbHelper.SUBJECTS_SUBJECT_COLUMN}=? " +
            "AND ${dbHelper.SUBJECTS_LANGUAGE_ID_COLUMN}=?"

    fun insert(subjectDto: SubjectDto) {
        val contentValues = ContentValues()
        contentValues.put(dbHelper.SUBJECTS_SUBJECT_COLUMN, subjectDto.subject)
        contentValues.put(dbHelper.SUBJECTS_LANGUAGE_ID_COLUMN, subjectDto.languageId)
        database.insert(dbHelper.SUBJECTS_TABLE_NAME, null, contentValues)
    }

    fun fetchForLanguageId(languageId: Int): Cursor {
        val columns = arrayOf(dbHelper.SUBJECTS_ID_COLUMN, dbHelper.SUBJECTS_SUBJECT_COLUMN)
        val cursor =
            database.query(dbHelper.SUBJECTS_TABLE_NAME, columns, WHERE_LANGUAGE_ID,
                arrayOf(languageId.toString()), null, null, null)
        cursor.moveToFirst()
        return cursor
    }

    fun fetchForSubjectAndLanguageId(subject: String, languageId: Int): Cursor {
        val columns = arrayOf(dbHelper.SUBJECTS_ID_COLUMN, dbHelper.SUBJECTS_SUBJECT_COLUMN,
            dbHelper.SUBJECTS_LANGUAGE_ID_COLUMN)
        val cursor = database.query(
            dbHelper.SUBJECTS_TABLE_NAME, columns, WHERE_SUBJECT_AND_LANGUAGE_ID,
            arrayOf(subject, languageId.toString()), null, null, null
        )
        cursor.moveToFirst()
        return cursor
    }

    fun update(subjectDto: SubjectDto): Int {
        val contentValues = ContentValues()
        contentValues.put(dbHelper.SUBJECTS_SUBJECT_COLUMN, subjectDto.subject)
        contentValues.put(dbHelper.SUBJECTS_LANGUAGE_ID_COLUMN, subjectDto.languageId)
        return database.update(
            dbHelper.SUBJECTS_TABLE_NAME, contentValues,
            "${dbHelper.SUBJECTS_ID_COLUMN} = ${subjectDto.id}", null
        )
    }

    fun delete(id: Int): Int{
        return database.delete(dbHelper.SUBJECTS_TABLE_NAME, WHERE_ID, arrayOf(id.toString()))
    }
}