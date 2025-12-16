package com.example.notes.presentation.screens.notes


import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.notes.domain.Note
import com.example.notes.presentation.ui.theme.OtherNotesColors
import com.example.notes.presentation.ui.theme.PinnedNotesColors
import com.example.notes.presentation.utils.DateFormatter
import com.example.notes.R


@Composable
fun NotesScreen(
    modifier: Modifier = Modifier,
    context: Context =  LocalContext.current.applicationContext,
    viewModel: NotesViewModel = viewModel{
        NotesViewModel(context)
    },
    onNoteClick1: (Note) -> Unit,// плэйсхолдер для коллбэка который мы реализуем в навигации
    onAddNoteClick: () -> Unit
) {
    // val state: State<NotesScreenState> = viewModel.state.collectAsState()// без делегата
    val state: NotesScreenState by viewModel.state.collectAsState()// В переменную типа State(работает
    // с Compose)записываем StateFlow из вьюмодели.
    //val currentState: NotesScreenState = state.value// с делегатом уже не нужно value
    // val scrollState = remember { ScrollState(0) }
    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddNoteClick,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                containerColor = MaterialTheme.colorScheme.primary,
                shape = CircleShape
            ) {
                Icon(

                    painter = painterResource(R.drawable.ic_add_note),
                    // imageVector = Icons.Default.Add,
                    contentDescription = "button add"
                )
            }
        }
    )

   { innerPadding ->
        LazyColumn(
            contentPadding = innerPadding,
            //.verticalScroll(rememberScrollState()),\\в лэйзи колум уже под капотом есть скрол стэйт
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {

                Title(
                    modifier = Modifier.padding(horizontal = 24.dp),
                    text = "All notes",

                    )
            }
            item {
                SearchBar(
                    modifier = Modifier.padding(horizontal = 24.dp),
                    query = state.query,
                    onQueryChange = {//в описании функции (в низу)мы просто сделали пустышку а тут описали функционал
                        viewModel.processCommand(NotesCommand.InputSearchQuery(it))//
                    // Это результат встроенной функции TextField onValueChange
                        // и мы кладем его под видом it в NotesCommand.InputSearchQuery, то есть мы таким образом получили ввод, соответственно query, а во вьюмодели мы заапдэйтим стэйт query а к нему привязан и стэйт экрана (у нас там два стэйта)
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
                    //items(state.pinnedNotes, key = { it.id }) { note ->// Это не нужно
                    itemsIndexed(
                        state.pinnedNotes,
                        key = { index, note -> note.id }) { index, note ->
                        NoteCard(
                            modifier = Modifier.widthIn(max = 160.dp),// ограничили количество знаков внутри заметки
                            note = note,//тут первая note это поле в композ функции которую мы ниже создали , а вторую ноут мы подставляем  как наш элемент
//                            onNoteClick = {
//                                viewModel.processCommand(NotesCommand.EditNote(it))
//                            },
                            onNoteClick = onNoteClick1, //передаем колбэк который в модифаере. Там мы сделали плэйсхолдер, его логику мы определим в NavGraph
                            //  onDoubleClick = {
                            //   viewModel.processCommand(NotesCommand.DeleteNote(note.id))
                            //  },
                            onLongClick = {
                                viewModel.processCommand(NotesCommand.SwitchPinnedStatus(note.id))
                            },
                            backgroundColor = PinnedNotesColors[index % PinnedNotesColors.size]
                        )
                    }

                }
            }




            item {

                Subtitle(
                    modifier = Modifier.padding(24.dp),
                    text = "Others"
                )
            }
            itemsIndexed(
                items = state.otherNotes,
                key = { index, item -> item.id }//привязываем ключ айтема в ui compose к заметке Note/ если заметок стало меньше, то айтемов тоже меньше
            ) { index, note: Note ->// тут можно _, note:Note ->
                NoteCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    note = note,
                    onNoteClick = onNoteClick1,//
                    // onNoteClick = {
                    //     viewModel.processCommand(NotesCommand.EditNote(it))//тут у заметки по которой кликнули будет меняться updatedAt и title edited а так как она в стэйте экрана компоуз перерисует ее
                    // },
                    // onDoubleClick = {
                    //     viewModel.processCommand(NotesCommand.DeleteNote(note.id))
                    // },
                    onLongClick = {
                        viewModel.processCommand(NotesCommand.SwitchPinnedStatus(note.id))
                    },


                    backgroundColor = OtherNotesColors[index % OtherNotesColors.size]
                )
            }


        }
    }

}

@Composable
private fun Title(
    modifier: Modifier = Modifier,
    text: String,//placeholder - сюда мы подставим значение при вызове функции

) {
    Box(
        modifier = modifier
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            modifier = modifier,
            text = text,// первый text это встроенный параметр у Text, мы в него передаем значение из плэйсхолдера
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
    }

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
                color = MaterialTheme.colorScheme.onSurface,
                shape = RoundedCornerShape(10.dp)
            ),
        value = query,
        onValueChange = onQueryChange,
        placeholder = {
            Text(
                text = "Search...",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
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
    onNoteClick: (Note) -> Unit,// это плэйс холдер. мы вдальнейшем туда подставим значение. Напри
    onLongClick: (Note) -> Unit,
    // onDoubleClick: (Note) -> Unit
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
                //  onDoubleClick = {
                //    onDoubleClick(note)
                //}

            )
            .padding(16.dp)
    ) {
        Text(

            text = note.title,
            fontSize = 14.sp,
            maxLines = 1,// максимум одна строка
            color = MaterialTheme.colorScheme.onSurface,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(
            modifier = Modifier.height(8.dp)
        )
        Text(

            text = DateFormatter.formatDateToString(note.updatedAt),//вызываем класс из Utils единственное место где мы его invoke
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,


            )
        Spacer(
            modifier = Modifier.height(24.dp)
        )
        Text(

            text = note.content,
            fontSize = 16.sp,
            maxLines = 3,// максимум три строки
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Medium,
            overflow = TextOverflow.Ellipsis

        )
    }

}
