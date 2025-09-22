package com.example.notes.presentation.screens.notes

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.notes.domain.Note

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
        modifier = Modifier
            .padding(top = 48.dp),
        //.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {


            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                state.pinnedNotes.forEach { note ->
                    NotesCard(
                        note = note,
                        onNoteClick = {
                            viewModel.proccessCommand(NotesCommand.SwitchPinnedStatus(note.id))
                        }
                    )

                }
            }
        }
        items(state.otherNotes){note: Note ->
            NotesCard(
                note = note,
                onNoteClick = {
                    viewModel.proccessCommand(NotesCommand.SwitchPinnedStatus(note.id))
                }
            )
        }
        state.otherNotes.forEach { note ->
            item {
                NotesCard(
                    note = note,
                    onNoteClick = {
                        viewModel.proccessCommand(NotesCommand.SwitchPinnedStatus(note.id))
                    }
                )
            }

//            Text(
//                modifier = Modifier.clickable {
//                    //viewModel.proccessCommand(NotesCommand.EditNote(note))
//                    viewModel.proccessCommand(NotesCommand.SwitchPinnedStatus(note.id))
//                },
//            text = "${note.title} - ${note.content}",
//            fontSize = 24.sp
//            )

        }

    }
}

@Composable
fun NotesCard(
    modifier: Modifier = Modifier,
    note: Note,
    onNoteClick: (Note) -> Unit
) {
    Text(
        modifier = Modifier.clickable {
            onNoteClick(note)
        },
        text = "${note.title} - ${note.content}",
        fontSize = 24.sp
    )
}