package com.storozhuk.learningvocabulary.application

import android.app.Application
import android.util.Log
import com.storozhuk.learningvocabulary.db.helper.DatabaseHelper
import com.storozhuk.learningvocabulary.db.repo.LanguagesRepository
import com.storozhuk.learningvocabulary.db.repo.SubjectsRepository
import com.storozhuk.learningvocabulary.db.repo.WordsRepository
import com.storozhuk.learningvocabulary.service.LanguagesService
import com.storozhuk.learningvocabulary.service.SubjectsService
import com.storozhuk.learningvocabulary.service.TestQuizService
import com.storozhuk.learningvocabulary.service.TestsGenerator
import com.storozhuk.learningvocabulary.service.WordsService

open class VocabularyContext : Application() {

    private var dbHelper: DatabaseHelper? = null
    private lateinit var wordsRepository: WordsRepository
    private lateinit var languagesRepository: LanguagesRepository
    private lateinit var subjectsRepository: SubjectsRepository
    private lateinit var wordsService: WordsService
    private lateinit var subjectsService: SubjectsService
    private lateinit var languagesService: LanguagesService
    private lateinit var testsGenerator: TestsGenerator
    private lateinit var testQuizService: TestQuizService

    override fun onCreate() {
        super.onCreate()
        setupDb()
        initServices()
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

    fun getWordsService(): WordsService {
        return wordsService
    }

    fun getSubjectsService(): SubjectsService {
        return subjectsService
    }

    fun getLanguagesService(): LanguagesService {
        return languagesService
    }

    fun getTestsGenerator(): TestsGenerator {
        return testsGenerator
    }

    fun getTestQuizService(): TestQuizService {
        return testQuizService
    }

    private fun setupDb() {
        initDbHelper()
        val database = dbHelper!!.writableDatabase
        languagesRepository = LanguagesRepository(database, dbHelper!!)
        wordsRepository = WordsRepository(database, dbHelper!!)
        subjectsRepository = SubjectsRepository(database, dbHelper!!)
    }

    private fun initServices() {
        wordsService = WordsService(wordsRepository)
        subjectsService = SubjectsService(subjectsRepository)
        languagesService = LanguagesService(languagesRepository)
        testsGenerator = TestsGenerator(wordsRepository)
        testQuizService = TestQuizService()
    }

    protected open fun initDbHelper() {
        dbHelper = DatabaseHelper(this.applicationContext)
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