package com.example.notes.domain

//class EditNoteUseCase(private val repository: NotesRepository) {
//    suspend operator fun invoke(note: Note) {
//        repository.editNote(note.copy(updatedAt = System.currentTimeMillis()))
//    }
//}
class EditNoteUseCase(private val repository: NotesRepository){
    suspend operator fun invoke(note:Note){
        repository.editNote(note.copy(updatedAt = System.currentTimeMillis()))
    }
}