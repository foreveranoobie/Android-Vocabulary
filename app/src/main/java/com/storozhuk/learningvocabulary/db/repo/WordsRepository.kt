package com.storozhuk.learningvocabulary.db.repo

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.storozhuk.learningvocabulary.db.helper.DatabaseHelper
import com.storozhuk.learningvocabulary.dto.data.WordDataDto
import com.storozhuk.learningvocabulary.dto.ui.WordDto

class WordsRepository(private val database: SQLiteDatabase, private val dbHelper: DatabaseHelper) {

    private val WHERE_ID_EQUALS = "${dbHelper.WORDS_ID_COLUMN}=?"
    private val WHERE_ORIGINAL_EQUALS = "${dbHelper.WORDS_ORIGINAL_COLUMN}=?"

    fun insert(wordDto: WordDataDto): Long{
        val contentValues = ContentValues()
        contentValues.put(dbHelper.WORDS_ORIGINAL_COLUMN, wordDto.original)
        contentValues.put(dbHelper.WORDS_TRANSLATE_COLUMN, wordDto.translate)
        contentValues.put(dbHelper.WORDS_SUBJECT_ID_COLUMN, wordDto.subjectId)
        return database.insert(dbHelper.WORDS_TABLE_NAME, null, contentValues)
    }

    fun fetch(): Cursor {
        val query = """SELECT w.id, w.original, w.translate, s.subject, s.language_id FROM words w
        INNER JOIN subjects s
        ON w.subject_id = s.id
        ORDER BY w.id
        """.trimMargin()
        val cursor = database.rawQuery(query, null)
        cursor?.moveToFirst()
        return cursor
    }

    fun fetchById(id: Int): Cursor{
        val cursor = database.query(dbHelper.WORDS_TABLE_NAME, arrayOf(dbHelper.WORDS_ID_COLUMN, dbHelper.WORDS_ORIGINAL_COLUMN,
            dbHelper.WORDS_TRANSLATE_COLUMN, dbHelper.WORDS_SUBJECT_ID_COLUMN), WHERE_ID_EQUALS,
            arrayOf(id.toString()), null, null, null)
        cursor.moveToFirst()
        return cursor
    }

    fun fetchByOriginal(original: String): Cursor{
        val cursor = database.query(dbHelper.WORDS_TABLE_NAME, arrayOf(dbHelper.WORDS_ID_COLUMN, dbHelper.WORDS_ORIGINAL_COLUMN,
            dbHelper.WORDS_TRANSLATE_COLUMN, dbHelper.WORDS_SUBJECT_ID_COLUMN), WHERE_ORIGINAL_EQUALS,
            arrayOf(original), null, null, null)
        cursor.moveToFirst()
        return cursor
    }

    fun fetchForLanguage(languageId: Int): Cursor {
        val query = """SELECT w.id, w.original, w.translate, s.subject, s.language_id FROM words w
        INNER JOIN subjects s
        ON w.subject_id = s.id
        WHERE s.language_id=${languageId}
        ORDER BY w.id
        """.trimMargin()
        val cursor = database.rawQuery(query, null)
        cursor?.moveToFirst()
        return cursor
    }

    fun fetchForLanguageAndSubject(languageId: Int, subject: String): Cursor {
        val query = """SELECT w.id, w.original, w.translate, s.subject FROM words w
        INNER JOIN subjects s
        ON w.subject_id = s.id
        WHERE s.language_id=${languageId} AND s.subject='${subject}'
        ORDER BY w.id
        """.trimMargin()
        val cursor = database.rawQuery(query, null)
        cursor?.moveToFirst()
        return cursor
    }

    fun update(wordDto: WordDataDto): Int{
        val contentValues = ContentValues()
        contentValues.put(dbHelper.WORDS_ORIGINAL_COLUMN, wordDto.original)
        contentValues.put(dbHelper.WORDS_TRANSLATE_COLUMN, wordDto.translate)
        contentValues.put(dbHelper.WORDS_SUBJECT_ID_COLUMN, wordDto.subjectId)
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

    fun cleanTable(){
        database.delete(dbHelper.WORDS_TABLE_NAME, null, null)
    }
}