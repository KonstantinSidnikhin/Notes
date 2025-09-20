package com.example.notes.presentation.screens.notes

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun NotesScreen(
    modifier: Modifier = Modifier,
    viewModel: NotesViewModel = viewModel()
) {
    // val state: State<NotesScreenState> = viewModel.state.collectAsState()// без делегата
    val state: NotesScreenState by viewModel.state.collectAsState()// Забираем стэйт экрана из вьюмодели
    //val currentState: NotesScreenState = state.value// с делегатом уже не нужно value
    // val scrollState = remember { ScrollState(0) }

    Column(
        modifier = Modifier
            .padding(top = 48.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            state.pinnedNotes.forEach { note ->
                Text(
                    modifier = Modifier.clickable {
                        viewModel.proccessCommand(NotesCommand.SwitchPinnedStatus(note.id))
                    },
                    text = "${note.title} - ${note.content}",
                    fontSize = 24.sp
                )
            }
        }
        state.otherNotes.forEach { note ->// тут вместо стэйт был каррент стэйт
            Text(
                modifier = Modifier.clickable {
                    //viewModel.proccessCommand(NotesCommand.EditNote(note))
                    viewModel.proccessCommand(NotesCommand.SwitchPinnedStatus(note.id))
                },
                text = "${note.title} - ${note.content}",
                fontSize = 24.sp
            )

        }

    }
}