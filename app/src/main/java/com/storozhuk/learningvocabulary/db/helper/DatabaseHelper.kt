package com.storozhuk.learningvocabulary.db.helper

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, "vocabulary", null, DB_VERSION) {
    val WORDS_TABLE_NAME = "words"
    val WORDS_ID_COLUMN = "id"
    val WORDS_ORIGINAL_COLUMN = "original"
    val WORDS_TRANSLATE_COLUMN = "translate"
    val WORDS_SUBJECT_COLUMN = "subject"
    val WORDS_LANGUAGE_ID_COLUMN = "language_id"

    val LANGUAGES_TABLE_NAME = "languages"
    val LANGUAGES_ID_COLUMN = "id"
    val LANGUAGES_LANGUAGE_COLUMN = "language"

    val SELECT_LANGUAGE_VALUE_WHERE_WORD_ID = """SELECT l.language FROM languages l
        INNER JOIN words w
        ON l.id = w.language_id
        WHERE w.id = %d
    """.trimMargin()

    companion object {
        private const val DB_VERSION = 1

        private const val CREATE_TABLE_WORDS =
            """CREATE TABLE words(
id INTEGER PRIMARY KEY AUTOINCREMENT, 
original TEXT NOT NULL, 
translate TEXT, 
subject TEXT,
language_id TEXT,
CONSTRAINT FK_LANGUAGE FOREIGN KEY (language_id) REFERENCES languages(id) ON DELETE CASCADE
);"""

        private const val CREATE_TABLE_LANGUAGES =
            """CREATE TABLE languages(
id INTEGER PRIMARY KEY AUTOINCREMENT, 
language TEXT NOT NULL UNIQUE
);"""

        private const val INSERT_DEFAULT_LANGUAGE = "INSERT INTO languages (language) VALUES ('No lang.')"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        System.err.println("DatabaseHelper: onCreate")
        db?.execSQL(CREATE_TABLE_LANGUAGES)
        db?.execSQL(CREATE_TABLE_WORDS)
        db?.execSQL(INSERT_DEFAULT_LANGUAGE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        System.err.println("DatabaseHelper: onUpgrade")
        db?.execSQL("DROP TABLE IF EXISTS $WORDS_TABLE_NAME")
        db?.execSQL("DROP TABLE IF EXISTS $LANGUAGES_TABLE_NAME")
        onCreate(db);
    }
}
