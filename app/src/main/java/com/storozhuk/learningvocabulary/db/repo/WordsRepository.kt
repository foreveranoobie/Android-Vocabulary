package com.storozhuk.learningvocabulary.db.repo

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.storozhuk.learningvocabulary.db.helper.DatabaseHelper
import com.storozhuk.learningvocabulary.dto.WordDto
import java.sql.SQLException
import java.util.Arrays

class WordsRepository(private val database: SQLiteDatabase, private val dbHelper: DatabaseHelper) {

    fun insert(wordDto: WordDto): Long{
        val contentValues: ContentValues = ContentValues()
        contentValues.put(dbHelper.WORDS_ORIGINAL_COLUMN, wordDto.original)
        contentValues.put(dbHelper.WORDS_TRANSLATE_COLUMN, wordDto.translate)
        contentValues.put(dbHelper.WORDS_SUBJECT_COLUMN, wordDto.subject)
        contentValues.put(dbHelper.WORDS_LANGUAGE_ID_COLUMN, wordDto.languageId)
        return database.insert(dbHelper.WORDS_TABLE_NAME, null, contentValues)
    }

    fun fetch(): Cursor {
        val query = """SELECT w.id, w.original, w.translate, w.subject, l.language FROM words w
        INNER JOIN languages l
        ON w.language_id = l.id
        ORDER BY w.id
        """.trimMargin()
        val cursor = database.rawQuery(query, null)
        cursor?.moveToFirst()
        return cursor
    }

    fun fetchForLanguage(languageId: Int): Cursor {
        val query = """SELECT w.id, w.original, w.translate, w.subject, l.language FROM words w
        INNER JOIN languages l
        ON w.language_id = l.id
        WHERE l.id=${languageId}
        ORDER BY w.id
        """.trimMargin()
        val cursor = database.rawQuery(query, null)
        cursor?.moveToFirst()
        return cursor
    }

    fun update(wordDto: WordDto): Int{
        val contentValues = ContentValues()
        contentValues.put(dbHelper.WORDS_ORIGINAL_COLUMN, wordDto.original)
        contentValues.put(dbHelper.WORDS_TRANSLATE_COLUMN, wordDto.translate)
        contentValues.put(dbHelper.WORDS_SUBJECT_COLUMN, wordDto.subject)
        contentValues.put(dbHelper.WORDS_LANGUAGE_ID_COLUMN, wordDto.languageId)
        return database.update(dbHelper.WORDS_TABLE_NAME, contentValues, "${dbHelper.WORDS_ID_COLUMN} = ${wordDto.id}", null)
    }

    fun findIdByOriginal(word: String): Int{
        val columns =  arrayOf(dbHelper.WORDS_ID_COLUMN)
        val cursor = database!!.query(dbHelper.WORDS_TABLE_NAME, columns, "${dbHelper.WORDS_ORIGINAL_COLUMN} LIKE '${word}'", null, null, null, null)
        cursor.moveToNext()
        val id = cursor.getInt(0)
        cursor.close()
        return id
    }

    fun findLanguageValueForWord(id: Int): String {
        val cursor = database.rawQuery(dbHelper.SELECT_LANGUAGE_VALUE_WHERE_WORD_ID.format(id), null, null)
        cursor.moveToFirst()
        val value = cursor.getString(0)
        cursor.close()
        return value
    }

    fun delete(id: Int): Int{
        return database.delete(dbHelper.WORDS_TABLE_NAME, "${dbHelper.WORDS_ID_COLUMN} = ${id}", null)
    }
}