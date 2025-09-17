package com.example.notes.data

import com.example.notes.domain.Note
import com.example.notes.domain.NotesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

object TestNotesRepositoryImpl : NotesRepository {
    private val notesListFlow = MutableStateFlow<List<Note>>(listOf())
    // override fun addNote(note: Note) {
    // val newNotes = notesListFlow.value.toMutableList()
    // newNotes.add(note)
    //  notesListFlow.value = newNotes
    // ------------------------------------
//        notesListFlow.update {
//            it.toMutableList().apply {
//                add(note)
//            }
//        }
    //       or
    // -------------------------------------------------------------------------
    //   notesListFlow.update {
    //      it + note
    //  }
    // }

    override fun addNote(title: String, content: String) {
        notesListFlow.update { oldList ->
            val note = Note(
                id = oldList.size,
                title = title,
                content = content,
                updatedAt = System.currentTimeMillis(),
                isPinned = false
            )
            oldList + note
        }
    }

    override fun deleteNote(noteId: Int) {
        notesListFlow.update { oldList ->
            oldList.toMutableList().apply {
                removeIf { it.id == noteId }
            }
        }

    }

    override fun editNote(note: Note) {
        notesListFlow.update { oldList ->
            oldList.map {
                if (it.id == note.id) {//если id в старом списке совпадает с id параметра note то мы
                    // вставляем note в список
                    note
                } else {
                    it
                }
            }
        }
    }

    override fun getAllNotes(): Flow<List<Note>> {
        return notesListFlow.asStateFlow()
    }

    override fun getNote(noteId: Int): Note {
        return notesListFlow.value.first { it.id == noteId }
    }

    override fun searchNotes(query: String): Flow<List<Note>> {
        return notesListFlow.map { currentList ->
            currentList.filter { it.title.contains(query) || it.content.contains(query) }

        }

    }

    override fun switchPinnedStatus(noteId: Int) {
        notesListFlow.update { oldList ->
            oldList.map {
                if (it.id == noteId) {
                    it.copy(isPinned = !it.isPinned)//меняем на противоположное значение
                } else {
                    it
                }
            }
        }
    }

}