@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.notes.presentation.screens.creation

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.example.notes.presentation.utils.DateFormatter


@Composable
fun CreateNoteScreen(
    modifier: Modifier = Modifier,
    viewModel: CreateNoteViewModel = hiltViewModel(),
    onFinished:()->Unit
) {
    val state = viewModel.state.collectAsState()
    val currentState = state.value
    when (currentState) {
        is CreateNoteState.Creation -> {

            Scaffold(
                modifier = modifier,
                topBar = {
                    TopAppBar(
                        title = {
                            Text(
                                text = "Create note",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color.Green,
                            navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                        ),
                        navigationIcon = {
                            Icon(
                                modifier = Modifier
                                    .padding(start = 16.dp, end = 80.dp)
                                    .clickable {
                                        viewModel.processCommand(CreateNoteCommand.Back)
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
                        value = currentState.title,
                        onValueChange = {
                            viewModel.processCommand(CreateNoteCommand.InputTitle(it))//срабатывают проверки заполнены ли другие поля и обновляется стэйт
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                        ),
                        textStyle = TextStyle(
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        placeholder = {
                            Text(

                                text = "Title",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface.copy(0.2f)
                            )
                        }
                    )
                    Text(
                        modifier = Modifier.padding(horizontal = 24.dp),
                        text = DateFormatter.formatCurrentDate(),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    TextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp)
                            .weight(1f),
                        value = currentState.content,
                        onValueChange = {
                            viewModel.processCommand(CreateNoteCommand.InputContent(it))//срабатывают проверки заполнены ли другие поля и обновляется стэйт
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
                            viewModel.processCommand(CreateNoteCommand.Save)
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

        CreateNoteState.Finished -> {
            LaunchedEffect(key1 = Unit) {  onFinished()}// key already mentioned before

        }
    }
}
