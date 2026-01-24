@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.notes.presentation.screens.creation

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.notes.domain.ContentItem
import com.example.notes.presentation.utils.DateFormatter


@Composable
fun CreateNoteScreen(
    modifier: Modifier = Modifier,
    viewModel: CreateNoteViewModel = hiltViewModel(),
    onFinished: () -> Unit
) {
    val state = viewModel.state.collectAsState()
    val currentState = state.value
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        {//сюда прилетает либо адрес обьекта который выбрал польз или нал.
                uri ->
            uri?.let {
                viewModel.processCommand(
                    CreateNoteCommand.AddImage(it)
                )
            }
        }
    )

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
                        },
                        actions = {
                            Icon(
                                modifier = Modifier
                                    .clickable { imagePicker.launch("image/*") }//Запускаем активити. В лончер передаем тип данных который нам нужен
                                    .padding(end = 24.dp),
                                imageVector = Icons.Default.Add,
                                contentDescription = "add photo",
                                tint = MaterialTheme.colorScheme.onSurface
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
                    LazyColumn(
                        modifier = Modifier.weight(1f)
                    ) {
                        currentState.content.forEachIndexed { index: Int, contentItem: ContentItem ->
                            item(key = index) {
                                when (contentItem) {
                                    is ContentItem.Image -> {
                                        TextContent(
                                            text = contentItem.url,
                                            onTextChanged = {}
                                        )
                                    }

                                    is ContentItem.Text -> {
                                        TextContent(
                                            text = contentItem.content,
                                            onTextChanged = {
                                                viewModel.processCommand(
                                                    CreateNoteCommand.InputContent(
                                                        content = it,
                                                        index = index
                                                    )
                                                )
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
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
                            disabledContentColor = MaterialTheme.colorScheme.primary.copy(
                                0.1f
                            )
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
            LaunchedEffect(key1 = Unit) { onFinished() }// key already mentioned before

        }
    }
}

@Composable
private fun TextContent(
    modifier: Modifier = Modifier,
    text: String,
    onTextChanged: (String) -> Unit
) {
    TextField(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),

        value = text,
        onValueChange = onTextChanged,
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
}
