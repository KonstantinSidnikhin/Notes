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
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

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
    private val query: MutableStateFlow<String> =
        MutableStateFlow("")

    private val _state: MutableStateFlow<NotesScreenState> =
        MutableStateFlow(NotesScreenState())//это коробка со стэйтФлоу экрана. в нем три поля query pinnedNotes otherNotes

    val state = _state.asStateFlow()//становится неизменяемым
    private val scope = CoroutineScope(Dispatchers.IO)

    init {
        addSomeNotes()
        query//1 это объект флоу и мы подписываемся на него при создании вьюмодели (это строка) она появляется только когда мы что то пишем в поле поиска
            .onEach { input: String ->//На каждый символ(это значение введенное пользователем
                _state.update { it.copy(query = input) }//обновляем стейт делая  копию текущего стэйта
                // в котором изменим свойства query
            }
            .flatMapLatest {//этот метод при изменении объекта флоу отменит старые подписки и подпишется на новый Flow
                    input: String ->
                if (input.isBlank()) {
                    getAllNotesUseCase()//2 тут мы переключаемся на этот поток данных и работаем с ним
                    // в блоке onEach
                } else {
                    searchNotesUseCase(input)//or this thread
                }
            }
            // тут очень важно что передается в следующий блок результат  searchNotesUseCase-  Flow<List<Note>>
            // и мы уже по этим элементам листа работаем
            .onEach {//Работаем с оьъектом Флоу
                    notes ->
                val pinnedNotes =
                    notes.filter { it.isPinned }//4 реагируем на каждый новый элемент в потоке
                val otherNotes = notes.filter { !it.isPinned }
                _state.update { it.copy(pinnedNotes = pinnedNotes, otherNotes = otherNotes) }
            }
            .launchIn(scope)//3 Подписываемся на этот поток
//        scope.launch {
//            query.collect {  }
//        }


    }


    private fun addSomeNotes() {
        repeat(100) {
            addNoteUseCase(title = "Title $it", content = "Content: $it")
        }
    }

    fun processCommand(command: NotesCommand) {
        when (command) {
            is NotesCommand.DeleteNote -> {
                deleteNoteUseCase(command.noteId)
            }

            is NotesCommand.EditNote -> {
                val note: Note = getNoteUseCase(command.note.id)
                val title = note.title
                //editNoteUseCase(note.copy(title = title + "edited"))
                editNoteUseCase(note.copy(title = title + "edited"))
            }

            is NotesCommand.InputSearchQuery -> {
                query.update { command.query.trim() }//тут запрос будет отправлен в обьект Flow(query) стэйт экрана меняетя и компоуз перерисовывает экран
            }

            is NotesCommand.SwitchPinnedStatus -> {
                switchPinnedStatusUseCase(command.noteId)
            }
        }
    }

}

sealed interface NotesCommand {
    data class InputSearchQuery(val query: String) : NotesCommand
    data class SwitchPinnedStatus(val noteId: Int) : NotesCommand

    //temp
    data class DeleteNote(val noteId: Int) : NotesCommand
    data class EditNote(val note: Note) : NotesCommand
}


data class NotesScreenState(
    val query: String = "",
    val pinnedNotes: List<Note> = listOf(),
    val otherNotes: List<Note> = listOf()
)
