package com.storozhuk.learningvocabulary.service

import com.storozhuk.learningvocabulary.DataProcessException
import com.storozhuk.learningvocabulary.db.repo.SubjectsRepository
import com.storozhuk.learningvocabulary.db.repo.WordsRepository
import com.storozhuk.learningvocabulary.dto.data.WordDataDto
import com.storozhuk.learningvocabulary.dto.ui.WordDto

class WordsService(
    val wordsRepository: WordsRepository,
) {
    fun addWord(wordDto: WordDto) {
        if (wordDto.original.isNotEmpty()) {
            if (wordsRepository.existsOriginalWithSubjectId(
                    wordDto.original,
                    wordDto.subjectId!!
                )
            ) {
                throw DataProcessException("Word ${wordDto.original} already exists in subject ${wordDto.subjectId}")
            } else {
                // Save word
                val dataDto =
                    WordDataDto(null, wordDto.original, wordDto.translate, wordDto.subjectId)
                wordsRepository.insert(dataDto)
            }
        } else {
            throw DataProcessException("Original should not be empty")
        }
    }
}