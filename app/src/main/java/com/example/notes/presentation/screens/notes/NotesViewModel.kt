package com.example.notes.presentation.screens.notes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notes.data.TestNotesRepositoryImpl
import com.example.notes.domain.GetAllNotesUseCase
import com.example.notes.domain.GetNoteUseCase
import com.example.notes.domain.Note
import com.example.notes.domain.SearchNotesUseCase
import com.example.notes.domain.SwitchPinnedStatusUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class NotesViewModel : ViewModel() {
    private val repository = TestNotesRepositoryImpl
    private val getAllNotesUseCase = GetAllNotesUseCase(repository)
    private val getNoteUseCase = GetNoteUseCase(repository)
    private val searchNotesUseCase = SearchNotesUseCase(repository)
    private val switchPinnedStatusUseCase = SwitchPinnedStatusUseCase(repository)
    private val query: MutableStateFlow<String> =
        MutableStateFlow("")
    private val _state: MutableStateFlow<NotesScreenState> =
        MutableStateFlow(NotesScreenState())//это коробка со стэйтФлоу экрана. в нем три поля query pinnedNotes otherNotes

    val state = _state.asStateFlow()//становится неизменяемым
    //private val scope = CoroutineScope(Dispatchers.IO)// под капотом назначена viewModelScope

    init {
        //addSomeNotes()
        query//1 это объект флоу и мы подписываемся на него при создании вьюмодели (это строка) она появляется только когда мы что то пишем в поле поиска
            .onEach { input: String ->
                _state.update { it.copy(query = input) }//here query is mutableStateFlow and state is not the same - it's a different mutableStateFlow

            }
            .flatMapLatest {//этот метод при изменении объекта флоу отменит старые подписки и подпишется на новый Flow
                    input: String ->
                if (input.isBlank()) {
                    getAllNotesUseCase()
                } else {
                    searchNotesUseCase(input)
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
            .launchIn(viewModelScope)//3 Подписываемся на этот поток
    }

    fun processCommand(command: NotesCommand) {
        viewModelScope.launch {// suspend функции можно запускать только внутри скоупа
            when (command) {

                is NotesCommand.InputSearchQuery -> {
                    query.update { command.query.trim() }//тут запрос будет отправлен в обьект Flow(query) стэйт экрана меняетя и компоуз перерисовывает экран
                }

                is NotesCommand.SwitchPinnedStatus -> {
                    switchPinnedStatusUseCase(command.noteId)
                }
            }
        }

    }
}

sealed interface NotesCommand {
    // эти  подклассы понятия не имеют что будет выполняться. Мы им логику навесим в ProcesCommand
    data class InputSearchQuery(val query: String) : NotesCommand
    data class SwitchPinnedStatus(val noteId: Int) : NotesCommand

}

data class NotesScreenState(
    val query: String = "",
    val pinnedNotes: List<Note> = listOf(),
    val otherNotes: List<Note> = listOf()
)


