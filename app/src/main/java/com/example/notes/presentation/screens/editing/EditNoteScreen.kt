@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.notes.presentation.screens.editing

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.notes.presentation.screens.creation.CreateNoteCommand
import com.example.notes.presentation.screens.creation.CreateNoteState
import com.example.notes.presentation.screens.creation.CreateNoteViewModel
import com.example.notes.presentation.screens.editing.EditNoteCommand.*
import com.example.notes.presentation.utils.DateFormatter


@Composable
fun EditNoteScreen(
    modifier: Modifier = Modifier,
    noteId: Int,// id of the note we pass with constructor
    viewModel: EditNoteViewModel = hiltViewModel(
        creationCallback = {factory:EditNoteViewModel.Factory->
            factory.create(noteId)
        }
    ),
    onFinished: () -> Unit
) {
    val state = viewModel.state.collectAsState()
    val currentState = state.value
    when (currentState) {
        is EditNoteState.Editing -> {

            Scaffold(
                modifier = modifier,
                topBar = {
                    TopAppBar(
                        title = {
                            Text(
                                text = "Edit note",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            // containerColor = Color.Transparent,
                            containerColor = Color.Green,
                            //navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
                            navigationIconContentColor = Color.Red,
                            //actionIconContentColor = MaterialTheme.colorScheme.onSurface,
                            actionIconContentColor = Color.Blue
                        ),
                        actions = {
                            Icon(
                                modifier = Modifier
                                    .padding(end = 16.dp)
                                    .clickable {
                                        viewModel.processCommand(EditNoteCommand.Delete)
                                    },
                                imageVector = Icons.Outlined.Delete,
                                contentDescription = "delete"
                            )
                        },
                        navigationIcon = {
                            Icon(
                                modifier = Modifier
                                    .padding(start = 16.dp, end = 8.dp)
                                    .clickable {
                                        viewModel.processCommand(EditNoteCommand.Back)
                                    },
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "back arrow"
                            )
                        }
                    )
                }
            ) { innerPadding ->
                Column(modifier = Modifier.padding(innerPadding)) {
                    TextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        value = currentState.note.title,//slightly changed
                        onValueChange = {
                            viewModel.processCommand(InputTitle(it))// мы передаем
                            // то что ввели с клавы под видом it во вьюмодель и она там работает с вводом.Видимо обновляет стэйт.
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Red,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Blue,
                            unfocusedIndicatorColor = Color.Transparent,
                        ),
                        textStyle = TextStyle(
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                            //color = Color.White
                        ),
                        placeholder = {
                            Text(

                                text = "Title",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                //color = MaterialTheme.colorScheme.onSurface.copy(0.2f)
                                color = Color.Cyan
                            )
                        }
                    )
                    Text(
                        modifier = Modifier.padding(horizontal = 24.dp),
                        text = DateFormatter.formatDateToString(currentState.note.updatedAt),//changed format
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    TextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp)
                            .weight(1f),
                        value = currentState.note.content,//slightly changed
                        onValueChange = {
                            viewModel.processCommand(InputContent(it))//срабатывают проверки заполнены ли другие поля и обновляется стэйт
                            // тут происходит главная магия мы обращаясь к вьюмодели для ее метода InputContent передаем результат onValueChange под видом it
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                        ),
                        textStyle = TextStyle(
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        placeholder = {
                            Text(

                                text = "write something below...",
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(0.2f)
                            )
                        }
                    )
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        onClick = {
                            viewModel.processCommand(EditNoteCommand.Save)
                        },
                        shape = RoundedCornerShape(10.dp),
                        enabled = currentState.isSaveEnabled,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            disabledContentColor = MaterialTheme.colorScheme.primary.copy(0.1f)
                        ),
                        //contentColor = MaterialTheme.colorScheme.onSurface,
                        // disabledContentColor = MaterialTheme.colorScheme.onSurface,
                    ) {
                        Text(
                            text = "Save Note"
                        )
                    }
                }

            }
        }

        EditNoteState.Finished -> {
            LaunchedEffect(key1 = Unit) { onFinished() }// key already mentioned before

        }

        EditNoteState.Initial -> {

        }
    }
}
