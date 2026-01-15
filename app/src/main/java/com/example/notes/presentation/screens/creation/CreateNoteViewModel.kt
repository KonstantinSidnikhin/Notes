package com.example.notes.presentation.screens.creation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notes.domain.AddNoteUseCase
import com.example.notes.domain.ContentItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateNoteViewModel @Inject constructor(private val addNoteUseCase: AddNoteUseCase) : ViewModel() {
    private val _state = MutableStateFlow<CreateNoteState>(CreateNoteState.Creation())//Здесь

    // в угловых скобках надо параметризировать что бы дать возможность и для Finished
    val state = _state.asStateFlow()

    fun processCommand(command: CreateNoteCommand) {
        when (command) {
            CreateNoteCommand.Back -> {

                _state.update { CreateNoteState.Finished }
            }

            is CreateNoteCommand.InputContent -> {//это если мы уже в состоянии Creation и начали например заполнять тайтл или контент
                _state.update { previousState ->
                    if (previousState is CreateNoteState.Creation) {//если мы в состоянии Creation
                        previousState.copy(// копируем для сохранения тайтла, если он есть
                            content = command.content,
                            isSaveEnabled = previousState.title.isNotBlank() && command.content.isNotBlank()
                        )
                    } else {
                        CreateNoteState.Creation(content = command.content)//а тут вызываем состояние Creation в первый раз поэтому copy не надо делать, там стэйта нет и просто назначаем полю контент значение из command
                    }

                }
            }

            is CreateNoteCommand.InputTitle -> {
                _state.update { previousState ->
                    if (previousState is CreateNoteState.Creation) {
                        previousState.copy(
                            title = command.title,
                            isSaveEnabled = previousState.content.isNotBlank() && command.title.isNotBlank()
                        )
                    } else {
                        CreateNoteState.Creation(title = command.title)
                    }

                }
            }

            CreateNoteCommand.Save -> {
                viewModelScope.launch {
                    _state.update { previousState ->
                        if (previousState is CreateNoteState.Creation) {
                            val title = previousState.title
                            val content = ContentItem.Text(previousState.content)
                            addNoteUseCase(title, listOf(content))//не забыть добавить заметку
                            CreateNoteState.Finished// мы создали заметку и все, и должны перейти на другой экран
                        } else {
                            previousState
                        }
                    }
                }
            }
        }
    }


}

sealed interface CreateNoteCommand {
    data class InputTitle(val title: String) : CreateNoteCommand
    data class InputContent(val content: String) : CreateNoteCommand
    data object Save : CreateNoteCommand
    data object Back : CreateNoteCommand

}



sealed interface CreateNoteState {
    data class Creation(
        val title: String = "",
        val content: String = "",
        val isSaveEnabled: Boolean = false
    ) : CreateNoteState

    data object Finished : CreateNoteState

}

