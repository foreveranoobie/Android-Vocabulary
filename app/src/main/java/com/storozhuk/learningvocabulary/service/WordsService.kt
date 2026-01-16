package com.storozhuk.learningvocabulary.service

import com.storozhuk.learningvocabulary.DataProcessException
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

    fun updateWord(wordDto: WordDto) {
        if (wordDto.original.isNotEmpty()) {
            val cursor = wordsRepository.findById(wordDto.id!!)
            val oldOriginal = cursor.getString(1)
            cursor.close()
            if (!oldOriginal.equals(wordDto.original) && wordsRepository.existsOriginalWithSubjectId(
                    wordDto.original,
                    wordDto.subjectId!!
                )
            ) {
                throw DataProcessException("Word {${wordDto.original}} already exists in subject")
            } else {
                val wordDataDto =
                    WordDataDto(wordDto.id, wordDto.original, wordDto.translate, wordDto.subjectId)
                wordsRepository.update(wordDataDto)
            }
        } else {
            throw DataProcessException("Original should not be empty")
        }
    }
}