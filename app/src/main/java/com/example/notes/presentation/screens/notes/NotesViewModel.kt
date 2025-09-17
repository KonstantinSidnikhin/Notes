package com.example.notes.presentation.screens.notes

import androidx.lifecycle.ViewModel
import com.example.notes.data.TestNotesRepositoryImpl
import com.example.notes.domain.AddNoteUseCase
import com.example.notes.domain.DeleteNoteUseCase
import com.example.notes.domain.EditNoteUseCase
import com.example.notes.domain.GetAllNotesUseCase
import com.example.notes.domain.GetNoteUseCase
import com.example.notes.domain.Note
import com.example.notes.domain.SearchNotesUseCase
import com.example.notes.domain.SwitchPinnedStatusUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class NotesViewModel : ViewModel() {
    private val repository = TestNotesRepositoryImpl
    private val addNoteUseCase = AddNoteUseCase(repository)
    private val editNoteUseCase = EditNoteUseCase(repository)
    private val deleteNoteUseCase = DeleteNoteUseCase(repository)
    private val getAllNotesUseCase = GetAllNotesUseCase(repository)
    private val getNoteUseCase = GetNoteUseCase(repository)
    private val searchNotesUseCase = SearchNotesUseCase(repository)
    private val switchPinnedStatusUseCase = SwitchPinnedStatusUseCase(repository)
    private val query: MutableStateFlow<String> = MutableStateFlow("")

    private val _state: MutableStateFlow<NotesScreenState> = MutableStateFlow(NotesScreenState())
    private val state = _state.asStateFlow()
    private val scope = CoroutineScope(Dispatchers.IO)

    init {
        query//1 это объект флоу и мы подписываемся на него при создании вьюмодели это строка
            .flatMapLatest {//этот метод при изменении объекта флоу отменит старые подписки
                if (it.isBlank()) {
                    getAllNotesUseCase()//2 тут мы переключаемся на этот поток данных и работаем с ним
                        // в блоке onEach
                } else {
                    searchNotesUseCase(it)//or this thread
                }
            }
            .onEach {//Работаем с оьъектом Флоу
                val pinnedNotes = it.filter { it.isPinned }//4 реагируем на каждый новый элемент в потоке
                val otherNotes = it.filter { !it.isPinned }
                _state.update { it.copy(pinnedNotes = pinnedNotes, otherNotes = otherNotes) }
            }
            .launchIn(scope)//3 Подписываемся на этот поток
//        scope.launch {
//            query.collect {  }
//        }

    }

    fun proccessCommand(command: NotesCommand) {
        when (command) {
            is NotesCommand.DeleteNote -> {
                deleteNoteUseCase(command.noteIde)
            }

            is NotesCommand.EditNote -> {
                val title = command.note.title
                editNoteUseCase(command.note.copy(title = title + "edited"))
            }

            is NotesCommand.InputSearchCommands -> {}
            is NotesCommand.SwitchPinnedStatus -> {
                switchPinnedStatusUseCase(command.noteId)
            }
        }
    }
}

sealed interface NotesCommand {
    data class InputSearchCommands(val query: String) : NotesCommand
    data class SwitchPinnedStatus(val noteId: Int) : NotesCommand

    //temp
    data class DeleteNote(val noteIde: Int) : NotesCommand
    data class EditNote(val note: Note) : NotesCommand
}

data class NotesScreenState(
    val query: String = "",
    val pinnedNotes: List<Note> = listOf(),
    val otherNotes: List<Note> = listOf()
)