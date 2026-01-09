package com.storozhuk.learningvocabulary.db.helper

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

open class DatabaseHelper : SQLiteOpenHelper {
    val WORDS_TABLE_NAME = "words"
    val WORDS_ID_COLUMN = "id"
    val WORDS_ORIGINAL_COLUMN = "original"
    val WORDS_TRANSLATE_COLUMN = "translate"
    val WORDS_SUBJECT_ID_COLUMN = "subject_id"

    val LANGUAGES_TABLE_NAME = "languages"
    val LANGUAGES_ID_COLUMN = "id"
    val LANGUAGES_LANGUAGE_COLUMN = "language"

    val SUBJECTS_TABLE_NAME = "subjects"
    val SUBJECTS_ID_COLUMN = "id"
    val SUBJECTS_SUBJECT_COLUMN = "subject"
    val SUBJECTS_LANGUAGE_ID_COLUMN = "language_id"

    val SELECT_LANGUAGE_VALUE_WHERE_WORD_ID = """SELECT l.language FROM languages l
        INNER JOIN words w
        ON l.id = w.language_id
        WHERE w.id = %d
    """.trimMargin()

    constructor(context: Context?) : super(context, "vocabulary", null, DB_VERSION)

    constructor(context: Context?, dbName: String?) : super(context, dbName, null, DB_VERSION)

    companion object {
        private const val DB_VERSION = 5

        private const val CREATE_TABLE_WORDS = """CREATE TABLE words(
id INTEGER PRIMARY KEY AUTOINCREMENT, 
original TEXT NOT NULL, 
translate TEXT, 
subject_id INTEGER NOT NULL,
CONSTRAINT FK_SUBJECT FOREIGN KEY (subject_id) REFERENCES subjects(id) ON DELETE CASCADE,
CONSTRAINT UC_ORIGINAL UNIQUE (original, subject_id)
);"""

        private const val CREATE_TABLE_LANGUAGES = """CREATE TABLE languages(
id INTEGER PRIMARY KEY AUTOINCREMENT, 
language TEXT NOT NULL UNIQUE
);"""

        private const val CREATE_TABLE_SUBJECTS = """CREATE TABLE subjects(
id INTEGER PRIMARY KEY AUTOINCREMENT,
subject TEXT NOT NULL,
language_id INTEGER NOT NULL,
CONSTRAINT FK_LANGUAGE FOREIGN KEY (language_id) REFERENCES languages(id) ON DELETE CASCADE
);"""

        private const val INSERT_DEFAULT_LANGUAGE =
            "INSERT INTO languages (language) VALUES ('No lang.')"
    }

    override fun onOpen(db: SQLiteDatabase) {
        db.execSQL("PRAGMA foreign_keys = ON;")
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(CREATE_TABLE_LANGUAGES)
        db?.execSQL(CREATE_TABLE_SUBJECTS)
        db?.execSQL(CREATE_TABLE_WORDS)
        db?.execSQL(INSERT_DEFAULT_LANGUAGE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL(
            "DELETE FROM $WORDS_TABLE_NAME " +
                    "WHERE rowid NOT IN(" +
                    "SELECT MIN(rowid) FROM $WORDS_TABLE_NAME GROUP BY $WORDS_ID_COLUMN, $WORDS_SUBJECT_ID_COLUMN" +
                    ")"
        )
        /*db?.execSQL("ALTER TABLE $WORDS_TABLE_NAME DROP CONSTRAINT UC_ORIGINAL")
        db?.execSQL("ALTER TABLE $WORDS_TABLE_NAME ADD CONSTRAINT UC_ORIGINAL UNIQUE (original, subject_id)")*/
    }
}
