package com.storozhuk.learningvocabulary.app

import com.storozhuk.learningvocabulary.application.VocabularyContext
import com.storozhuk.learningvocabulary.db.helper.DatabaseHelper

class TestApplicationContext : VocabularyContext() {

    override fun initDbHelper() {
        setDbHelper(TestDatabaseHelper(this.applicationContext))
    }

    public override fun getDbHelper(): DatabaseHelper? {
        return super.getDbHelper()
    }
}