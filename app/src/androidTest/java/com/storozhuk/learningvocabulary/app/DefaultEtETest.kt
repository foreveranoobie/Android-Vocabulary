package com.storozhuk.learningvocabulary.app

import androidx.test.core.app.ApplicationProvider
import com.storozhuk.learningvocabulary.db.repo.LanguagesRepository
import com.storozhuk.learningvocabulary.db.repo.SubjectsRepository
import org.junit.Before

open class DefaultEtETest {

    protected lateinit var context: TestApplicationContext
    protected lateinit var testDbHelper: TestDatabaseHelper

    @Before
    fun setup(){
        context = ApplicationProvider.getApplicationContext() as TestApplicationContext
        testDbHelper = context.getDbHelper() as TestDatabaseHelper
        testDbHelper.cleanDb()
    }
}