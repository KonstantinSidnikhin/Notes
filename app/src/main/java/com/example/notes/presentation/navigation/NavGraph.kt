package com.example.notes.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.notes.presentation.screens.creation.CreateNoteScreen
import com.example.notes.presentation.screens.editing.EditNoteScreen
import com.example.notes.presentation.screens.notes.NotesScreen

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Screen.Notes.route
    ) {
        composable(Screen.Notes.route) {
            NotesScreen(//тут заглавный экран
                onNoteClick1 = {// в функции NotesScreen у нас был плэйс холдер для коллбэка, вот тут мы реализуем его.
                    navController.navigate(Screen.EditNote.route)

                },
                onAddNoteClick = {
                    navController.navigate(Screen.CreateNote.route)
                }
            )
        }
        composable(Screen.CreateNote.route) {
            CreateNoteScreen(
                onFinished = {
                    navController.navigate(Screen.Notes.route)
                }
            )
        }
        composable(Screen.EditNote.route) {
            EditNoteScreen(
                noteId = 5,
                onFinished = {
                    navController.navigate(Screen.Notes.route)

                }
            )

        }
    }
}

@Composable
fun CustomNavGraph() {
    val screen = remember { mutableStateOf<CustomScreen>(CustomScreen.Notes) }
    val currentScreen = screen.value
    when (currentScreen) {
        CustomScreen.CreateNote -> {
            CreateNoteScreen(
                onFinished = {
                    screen.value = CustomScreen.Notes
                }
            )

        }

        is CustomScreen.EditNote -> {
            EditNoteScreen(
                noteId = currentScreen.noteId,
                onFinished = {
                    screen.value = CustomScreen.Notes// when finish we get back to main screen
                }
            )
        }

        CustomScreen.Notes -> {
            NotesScreen(//тут заглавный экран
                onNoteClick1 = {// в функции NotesScreen у нас был плэйс холдер для коллбэка, вот тут мы реализуем его.
                    screen.value = CustomScreen.EditNote(it.id)
                },
                onAddNoteClick = {
                    screen.value = CustomScreen.CreateNote
                }
            )
        }
    }

}

sealed class Screen(val route: String) {
    data object Notes : Screen("notes")
    data object CreateNote : Screen("create_note")
    data object EditNote : Screen("edit_note")
}

sealed interface CustomScreen {
    data object Notes : CustomScreen
    data object CreateNote : CustomScreen
    data class EditNote(val noteId: Int) : CustomScreen
}