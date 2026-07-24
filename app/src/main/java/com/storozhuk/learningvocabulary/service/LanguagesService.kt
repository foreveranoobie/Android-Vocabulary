package com.storozhuk.learningvocabulary.service

import com.storozhuk.learningvocabulary.db.repo.LanguagesRepository
import com.storozhuk.learningvocabulary.dto.ui.LanguageDto

class LanguagesService(val languagesRepository: LanguagesRepository) {
    fun getAllLanguages(): List<LanguageDto> {
        val result = ArrayList<LanguageDto>()
        val cursor = languagesRepository.fetch()
        while (!cursor.isAfterLast) {
            result.add(
                LanguageDto(
                    cursor.getInt(0),
                    cursor.getString(1)
                )
            )
            cursor.moveToNext()
        }
        cursor.close()
        return result
    }

    fun getLanguageIdByName(language: String): Int {
        return languagesRepository.fetchId(language)
    }
}