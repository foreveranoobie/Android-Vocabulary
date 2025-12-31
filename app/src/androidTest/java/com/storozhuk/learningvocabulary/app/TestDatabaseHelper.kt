package com.storozhuk.learningvocabulary.app

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.storozhuk.learningvocabulary.db.helper.DatabaseHelper
import com.storozhuk.learningvocabulary.dto.data.SubjectDto
import com.storozhuk.learningvocabulary.dto.data.WordDataDto
import com.storozhuk.learningvocabulary.dto.ui.LanguageDto


class TestDatabaseHelper(context: Context) : DatabaseHelper(context, null) {

    private val database: SQLiteDatabase

    init {
        database = writableDatabase
    }

    fun insertLanguage(languageDto: LanguageDto) {
        database.execSQL("INSERT INTO languages (id, language) VALUES (${languageDto.id}, '${languageDto.language}')")
    }

    fun insertSubject(subjectDto: SubjectDto){
        database.execSQL("INSERT INTO subjects (id, subject, language_id) VALUES (${subjectDto.id}, '${subjectDto.subject}', ${subjectDto.languageId})")
    }

    fun insertWord(wordDataDto: WordDataDto){
        database.execSQL("INSERT INTO words (id, original, translate, subject_id) VALUES (${wordDataDto.id}, '${wordDataDto.original}', '${wordDataDto.translate}', ${wordDataDto.subjectId})")
    }

    fun cleanDb(){
        database.execSQL("DELETE FROM languages")
        database.execSQL("DELETE FROM subjects")
        database.execSQL("DELETE FROM words")
        database.execSQL("UPDATE sqlite_sequence SET seq = 0 WHERE name='languages'");
        database.execSQL("UPDATE sqlite_sequence SET seq = 0 WHERE name='subjects'");
        database.execSQL("UPDATE sqlite_sequence SET seq = 0 WHERE name='words'");
        database.execSQL("INSERT INTO languages (language) VALUES ('No lang.')")
    }
}