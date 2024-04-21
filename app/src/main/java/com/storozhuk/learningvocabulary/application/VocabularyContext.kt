package com.storozhuk.learningvocabulary.application

import android.app.Application
import com.storozhuk.learningvocabulary.db.helper.DatabaseHelper
import com.storozhuk.learningvocabulary.db.repo.LanguagesRepository
import com.storozhuk.learningvocabulary.db.repo.WordsRepository
import com.storozhuk.learningvocabulary.dto.LanguageDto
import com.storozhuk.learningvocabulary.dto.WordDto

class VocabularyContext : Application() {

    private var dbHelper: DatabaseHelper? = null
    lateinit var wordsRepository: WordsRepository
    lateinit var languagesRepository: LanguagesRepository

    override fun onCreate() {
        super.onCreate()
        System.err.println("Establishing DB connection")
        dbHelper = DatabaseHelper(this.applicationContext)
        val database = dbHelper!!.writableDatabase
        languagesRepository = LanguagesRepository(database, dbHelper!!)
        wordsRepository = WordsRepository(database, dbHelper!!)
        //initTestData()
    }

    private fun initTestData(){
        languagesRepository.insert(LanguageDto(null, "No lang"))
        languagesRepository.insert(LanguageDto(null, "English"))
    }

    override fun onTerminate() {
        dbHelper?.close()
        super.onTerminate()
    }
}