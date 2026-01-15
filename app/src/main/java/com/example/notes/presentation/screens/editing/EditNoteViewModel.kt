package com.example.notes.presentation.screens.editing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notes.domain.ContentItem
import com.example.notes.domain.DeleteNoteUseCase
import com.example.notes.domain.EditNoteUseCase
import com.example.notes.domain.GetNoteUseCase
import com.example.notes.domain.Note
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = EditNoteViewModel.Factory::class)
class EditNoteViewModel @AssistedInject constructor(
    @Assisted("noteId") private val noteId: Int,
    private val editNoteUseCase: EditNoteUseCase,
    private val getNoteUseCase: GetNoteUseCase,
    private val deleteNoteUseCase: DeleteNoteUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<EditNoteState>(EditNoteState.Initial)//Здесь

    // в угловых скобках надо параметризировать что бы дать возможность и для Finished
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            _state.update {
                val note = getNoteUseCase(noteId)//noteId came in constructor of the class
                EditNoteState.Editing(note)
            }
        }
    }

    fun processCommand(command: EditNoteCommand) {
        when (command) {
            EditNoteCommand.Back -> {

                _state.update { EditNoteState.Finished }
            }

            is EditNoteCommand.InputContent -> {//это если мы уже в состоянии Creation и начали например заполнять тайтл или контент
                _state.update { previousState ->
                    if (previousState is EditNoteState.Editing) {
                        val newContent = ContentItem.Text(content = command.content)
                        val newNote =
                            previousState.note.copy(content = listOf(newContent))//взяли заметку из текущего стэйта и изменили в ней поле контент
                        previousState.copy(note = newNote)//взяли текущий стэйт экрана и вставили в него заметку с измененным полем контент
                    } else {
                        previousState// если не состояние Editing значит ошибка. Но мы просто сохраним предыдущее состояние
                    }

                }
            }

            is EditNoteCommand.InputTitle -> {
                _state.update { previousState ->
                    if (previousState is EditNoteState.Editing) {
                        val newNote = previousState.note.copy(title = command.title)
                        previousState.copy(note = newNote)
                    } else {
                        previousState// если не состояние Editing значит ошибка. Но мы просто сохраним предыдущее состояние
                    }

                }
            }

            EditNoteCommand.Save -> {
                viewModelScope.launch {
                    _state.update { previousState ->
                        if (previousState is EditNoteState.Editing) {
                            val note = previousState.note
                            editNoteUseCase(note)
                            EditNoteState.Finished// мы создали заметку и все, и должны перейти на другой экран
                        } else {
                            previousState
                        }
                    }
                }
            }

            EditNoteCommand.Delete -> {
                viewModelScope.launch {
                    _state.update { previousState ->
                        if (previousState is EditNoteState.Editing) {
                            val note = previousState.note
                            deleteNoteUseCase(note.id)
                            EditNoteState.Finished// мы создали заметку и все, и должны перейти на другой экран
                        } else {
                            previousState
                        }
                    }
                }
            }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("noteId") noteId: Int
        ): EditNoteViewModel
    }

}

sealed interface EditNoteCommand {
    data class InputTitle(val title: String) : EditNoteCommand
    data class InputContent(val content: String) : EditNoteCommand
    data object Save : EditNoteCommand
    data object Back : EditNoteCommand
    data object Delete : EditNoteCommand

}


sealed interface EditNoteState {
    data object Initial :
        EditNoteState// если бы мы грузили данные с интернета то тут бы показывали прогресс бар например

    data class Editing(
        val note: Note
    ) : EditNoteState {
        val isSaveEnabled: Boolean
            get() {
                return when {
                    note.title.isBlank() -> false////тут если тайтл не пустой и контент не пустой
                    note.content.isEmpty() -> false
                    else -> {
                        note.content.any {
                            it !is ContentItem.Text || it.content.isNotBlank()//если есть хотя бы картинка (не текст) то сохраняем.
                        }
                    }
                }
            }

    }

    data object Finished : EditNoteState

}
