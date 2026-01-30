package com.example.notes.presentation.screens.creation

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notes.domain.AddNoteUseCase
import com.example.notes.domain.ContentItem
import com.example.notes.domain.ContentItem.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateNoteViewModel @Inject constructor(private val addNoteUseCase: AddNoteUseCase) :
    ViewModel() {
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
                        val newContent =
                            previousState.content//Изображения (не текстовые элементы) не удаляются при редактировании текста
                                //Они остаются в списке content независимо от содержимого текстовых полей
                                //Сохранение изображений происходит только при их добавлении через AddImage
                                // Проверка возможности сохранения (isSaveEnabled) учитывает наличие изображений:
                                .mapIndexed { index: Int, contentItem: ContentItem ->
                                    if (index == command.index && contentItem is ContentItem.Text) {
                                        contentItem.copy(content = command.content)
                                    } else {
                                        contentItem
                                    }

                                }
                        previousState.copy(// копируем для сохранения тайтла, если он есть
                            content = newContent

                        )
                    } else {
                        previousState
                    }

                }
            }

            is CreateNoteCommand.InputTitle -> {
                _state.update { previousState ->
                    if (previousState is CreateNoteState.Creation) {
                        previousState.copy(
                            title = command.title

                        )
                    } else {
                        previousState
                    }

                }
            }

            CreateNoteCommand.Save -> {
                viewModelScope.launch {
                    _state.update { previousState ->
                        if (previousState is CreateNoteState.Creation) {
                            val title = previousState.title
                            val content =
                                previousState.content.filter {//оставим в контенте не текст(картинки) или если текст не пустой
                                    it !is ContentItem.Text || it.content.isNotBlank()
                                }
                            addNoteUseCase(title, content)//не забыть добавить заметку
                            CreateNoteState.Finished// мы создали заметку и все, и должны перейти на другой экран
                        } else {
                            previousState
                        }
                    }
                }
            }

            is CreateNoteCommand.AddImage -> {
                _state.update { previousState: CreateNoteState ->
                    if (previousState is CreateNoteState.Creation) {

                        val newItems = previousState.content.toMutableList()
                        val lastItem = newItems.last()
                        if (lastItem is ContentItem.Text && lastItem.content.isBlank()) {
                            newItems.removeAt(newItems.lastIndex)
                        }
                        newItems.add(Image(command.uri.toString()))
                        newItems.add(Text(""))
                        previousState.copy(content = newItems)
                    } else {
                        previousState
                    }

                }
            }

            is CreateNoteCommand.DeleteImage -> {
                _state.update { previousState: CreateNoteState ->
                    if (previousState is CreateNoteState.Creation) {

                        previousState.content.toMutableList().apply {
                            removeAt(command.index)
                        }.let {
                            previousState.copy(content = it)
                        }
                    } else {
                        previousState
                    }

                }
            }
        }
    }


}

sealed interface CreateNoteCommand {
    data class AddImage(val uri: Uri) : CreateNoteCommand
    data class InputTitle(val title: String) : CreateNoteCommand
    data class InputContent(val content: String, val index: Int) : CreateNoteCommand
    data object Save : CreateNoteCommand
    data object Back : CreateNoteCommand
    data class DeleteImage(val index: Int) : CreateNoteCommand

}


sealed interface CreateNoteState {
    data class Creation(
        val title: String = "",
        val content: List<ContentItem> = listOf(ContentItem.Text("")),

        ) : CreateNoteState {
        val isSaveEnabled: Boolean
            get() {
                return when {
                    title.isBlank() -> false////тут если тайтл не пустой и контент не пустой
                    content.isEmpty() -> false
                    else -> {
                        content.any {
                            it !is ContentItem.Text || it.content.isNotBlank()//если есть хотя бы картинка (не текст) то сохраняем.
                        }
                    }
                }
            }
    }

    data object Finished : CreateNoteState

}

