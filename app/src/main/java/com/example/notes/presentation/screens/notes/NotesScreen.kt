package com.example.notes.presentation.screens.notes

import android.R
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.notes.domain.Note
import com.example.notes.presentation.ui.theme.Green
import com.example.notes.presentation.ui.theme.Yellow200

@Composable
fun NotesScreen(
    modifier: Modifier = Modifier,
    viewModel: NotesViewModel = viewModel()
) {
    // val state: State<NotesScreenState> = viewModel.state.collectAsState()// без делегата
    val state: NotesScreenState by viewModel.state.collectAsState()// В переменную типа State(работает
    // с Compose)записываем StateFlow из вьюмодели.
    //val currentState: NotesScreenState = state.value// с делегатом уже не нужно value
    // val scrollState = remember { ScrollState(0) }

    LazyColumn(
        modifier = modifier
            .padding(top = 48.dp, start = 24.dp, end = 24.dp),
        //.verticalScroll(rememberScrollState()),\\в лэйзи колум уже под капотом есть срол стэйт
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Title(text = "All notes")
        }
        item {
            SearchBar(
                modifier = Modifier.padding(horizontal = 24.dp),
                query = state.query,
                // query = "something",
                onQueryChange = {//в описании функции (в низу)мы просто сделали пустышку а тут описали функционал
                    viewModel.proccessCommand(NotesCommand.InputSearchQuery(it))//тут меняют в стэйте экрана параметр query
                    // и экран перерисовывает заметки содержащие строку == query в контенте или тайтле
                }
            )
        }
        item {

            Subtitle(
                modifier = Modifier.padding(horizontal = 24.dp),
                text = "Pinned"
            )

        }

        item {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
//                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 24.dp)
            ) {
                state.pinnedNotes.forEach { note ->
                    item(key = note.id) {//привязываем ключ айтема в ui compose к заметке Note/ если заметок стало меньше, то айтемов тоже мен
                        NoteCard(
                            note = note,
                            onNoteClick = {
                                viewModel.proccessCommand(NotesCommand.EditNote(it))
                            },
                            onDoubleClick = {
                                viewModel.proccessCommand(NotesCommand.DeleteNote(note.id))
                            },
                            onLongClick = {
                                viewModel.proccessCommand(NotesCommand.SwitchPinnedStatus(note.id))
                            },
                            backgroundColor = Yellow200
                        )
                    }

                }
            }
        }
        item {
            Subtitle(text = "Others")
        }
        items(
            state.otherNotes,
            key = { it.id }//привязываем ключ айтема в ui compose к заметке Note/ если заметок стало меньше, то айтемов тоже меньше
        ) { note: Note ->
            NoteCard(
                modifier = Modifier.fillMaxWidth()
                .padding(horizontal = 24.dp),
                note = note,
                onNoteClick = {
                    viewModel.proccessCommand(NotesCommand.EditNote(it))
                },
                onDoubleClick = {
                    viewModel.proccessCommand(NotesCommand.DeleteNote(note.id))
                },
                onLongClick = {
                    viewModel.proccessCommand(NotesCommand.SwitchPinnedStatus(note.id))
                },
                
                backgroundColor = Green
            )
        }
//        state.otherNotes.forEach { note ->
//            item {
//                NotesCard(
//                    note = note,
//                    onNoteClick = {
//                        viewModel.proccessCommand(NotesCommand.SwitchPinnedStatus(note.id))
//                    }
//                )
//            }

//            Text(
//                modifier = Modifier.clickable {
//                    //viewModel.proccessCommand(NotesCommand.EditNote(note))
//                    viewModel.proccessCommand(NotesCommand.SwitchPinnedStatus(note.id))
//                },
//            text = "${note.title} - ${note.content}",
//            fontSize = 24.sp
//            )

        //      }

    }
}

@Composable
private fun Title(
    modifier: Modifier = Modifier,
    text: String,

    ) {
    Text(
        modifier = modifier,
        text = text,
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground
    )
}

@Composable
private fun SearchBar(
    modifier: Modifier = Modifier,
    query: String,
    onQueryChange: (String) -> Unit
) {
    TextField(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.onSurface)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                shape = RoundedCornerShape(10.dp)
            ),

        value = query,
        onValueChange = onQueryChange,
        placeholder = {
            Text(
                text = "Search...",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.onSurfaceVariant,
            unfocusedContainerColor = MaterialTheme.colorScheme.onSurfaceVariant,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,

            ),
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search notes",
                tint = MaterialTheme.colorScheme.onSurface
            )
        },
        shape = RoundedCornerShape(10.dp)

    )
}

@Composable
private fun Subtitle(
    modifier: Modifier = Modifier,
    text: String
) {
    Text(
        modifier = modifier,
        text = text,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp
    )
}

@Composable
fun NoteCard(
    modifier: Modifier = Modifier,
    note: Note,
    backgroundColor: Color,
    onNoteClick: (Note) -> Unit,
    onLongClick: (Note) -> Unit,
    onDoubleClick: (Note) -> Unit
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .combinedClickable(
                onClick = {
                    onNoteClick(note)
                },
                onLongClick = {
                    onLongClick(note)
                },
                onDoubleClick = {
                    onDoubleClick(note)
                }

            )
    ) {
        Text(

            text = note.title,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(

            text = note.updatedAt.toString(),
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(

            text = note.content,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Medium

        )
    }

}

//=======================
//@Composable
//private fun SearchBarTwo(
//
//
//}

