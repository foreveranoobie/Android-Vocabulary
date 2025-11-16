package com.storozhuk.learningvocabulary.application

import android.app.Application
import com.storozhuk.learningvocabulary.db.helper.DatabaseHelper
import com.storozhuk.learningvocabulary.db.repo.LanguagesRepository
import com.storozhuk.learningvocabulary.db.repo.SubjectsRepository
import com.storozhuk.learningvocabulary.db.repo.WordsRepository
import com.storozhuk.learningvocabulary.dto.ui.LanguageDto

class VocabularyContext : Application() {

    private var dbHelper: DatabaseHelper? = null
    lateinit var wordsRepository: WordsRepository
    lateinit var languagesRepository: LanguagesRepository
    lateinit var subjectsRepository: SubjectsRepository


    override fun onCreate() {
        super.onCreate()
        System.err.println("Establishing DB connection")
        dbHelper = DatabaseHelper(this.applicationContext)
        val database = dbHelper!!.writableDatabase
        languagesRepository = LanguagesRepository(database, dbHelper!!)
        wordsRepository = WordsRepository(database, dbHelper!!)
        subjectsRepository = SubjectsRepository(database, dbHelper!!)
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