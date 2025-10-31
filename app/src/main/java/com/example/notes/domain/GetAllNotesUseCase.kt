package com.example.notes.domain

import kotlinx.coroutines.flow.Flow

class GetAllNotesUseCase (private val repository: NotesRepository){
   suspend operator fun invoke(): Flow<List<Note>> {
       return repository.getAllNotes()
    }
}