package com.storozhuk.learningvocabulary.db.repo

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.storozhuk.learningvocabulary.db.helper.DatabaseHelper
import com.storozhuk.learningvocabulary.dto.LanguageDto

class LanguagesRepository(private val database: SQLiteDatabase, private val dbHelper: DatabaseHelper) {
    fun insert(languageDto: LanguageDto): Long{
        val contentValues = ContentValues()
        contentValues.put(dbHelper.LANGUAGES_LANGUAGE_COLUMN, languageDto.language)
        return database.insert(dbHelper.LANGUAGES_TABLE_NAME, null, contentValues)
    }

    fun fetch(): Cursor{
        val columns =
            arrayOf(dbHelper.LANGUAGES_ID_COLUMN, dbHelper.LANGUAGES_LANGUAGE_COLUMN)
        val cursor =
            database.query(dbHelper.LANGUAGES_TABLE_NAME, columns, null, null, null, null, dbHelper.LANGUAGES_ID_COLUMN);
        cursor?.moveToFirst()
        return cursor
    }

    fun update(languageDto: LanguageDto): Int {
        val contentValues = ContentValues()
        contentValues.put(dbHelper.LANGUAGES_LANGUAGE_COLUMN, languageDto.language)
        return database.update(dbHelper.LANGUAGES_TABLE_NAME, contentValues,
            "${dbHelper.LANGUAGES_ID_COLUMN} = ${languageDto.id}", null)
    }


    fun updateHavingValue(language: String, updateTo: String): Int {
        val contentValues = ContentValues()
        contentValues.put(dbHelper.LANGUAGES_LANGUAGE_COLUMN, updateTo)
        return database.update(dbHelper.LANGUAGES_TABLE_NAME, contentValues,
            "${dbHelper.LANGUAGES_ID_COLUMN} = ${language}", null)
    }

    fun delete(id: Int): Int{
        return database.delete(dbHelper.LANGUAGES_TABLE_NAME, "${dbHelper.LANGUAGES_ID_COLUMN} = ${id}", null)
    }

    fun fetchId(language: String): Int {
        val columns =
            arrayOf(dbHelper.LANGUAGES_ID_COLUMN)
        val cursor =
            database.query(dbHelper.LANGUAGES_TABLE_NAME, columns, "${dbHelper.LANGUAGES_LANGUAGE_COLUMN} LIKE '${language}'", null, null, null, null);
        cursor?.moveToFirst()
        val id = cursor.getInt(0)
        cursor.close()
        return id
    }
}