package com.storozhuk.learningvocabulary.application

import android.app.Application
import android.util.Log
import com.storozhuk.learningvocabulary.db.helper.DatabaseHelper
import com.storozhuk.learningvocabulary.db.repo.LanguagesRepository
import com.storozhuk.learningvocabulary.db.repo.SubjectsRepository
import com.storozhuk.learningvocabulary.db.repo.WordsRepository

open class VocabularyContext : Application() {

    private var dbHelper: DatabaseHelper? = null
    private lateinit var wordsRepository: WordsRepository
    private lateinit var languagesRepository: LanguagesRepository
    private lateinit var subjectsRepository: SubjectsRepository

    override fun onCreate() {
        super.onCreate()
        setupDb()
        Log.d("TestApp", "Database path: ${getDbHelper()?.readableDatabase!!.path}")
    }

    fun getWordsRepository(): WordsRepository {
        return wordsRepository
    }

    fun getLanguagesRepository(): LanguagesRepository {
        return languagesRepository
    }

    fun getSubjectsRepository(): SubjectsRepository {
        return subjectsRepository
    }

    private fun setupDb() {
        initDbHelper()
        val database = dbHelper!!.writableDatabase
        languagesRepository = LanguagesRepository(database, dbHelper!!)
        wordsRepository = WordsRepository(database, dbHelper!!)
        subjectsRepository = SubjectsRepository(database, dbHelper!!)
    }

    protected open fun initDbHelper() {
        dbHelper = DatabaseHelper(this.applicationContext)
    }

    protected fun setLanguagesRepository(languagesRepository: LanguagesRepository) {
        this.languagesRepository = languagesRepository
    }

    protected fun setWordsRepository(wordsRepository: WordsRepository) {
        this.wordsRepository = wordsRepository
    }

    protected fun setSubjectsRepository(subjectsRepository: SubjectsRepository) {
        this.subjectsRepository = subjectsRepository;
    }

    protected fun setDbHelper(dbHelper: DatabaseHelper) {
        this.dbHelper = dbHelper
    }

    protected open fun getDbHelper(): DatabaseHelper? {
        return dbHelper
    }

    override fun onTerminate() {
        dbHelper?.close()
        super.onTerminate()
    }
}