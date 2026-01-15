package com.example.notes.presentation.navigation

import android.os.Bundle
import androidx.compose.runtime.Composable
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
        startDestination = Screen.Notes.route// route это строка с названием экрана
    ) {
        composable(Screen.Notes.route) {// если строка "notes" то выполнится функция далее. По сути composable это замена when(adress){...}
            NotesScreen(
                onNoteClick1 = {// в функции NotesScreen у нас был плэйс холдер для коллбэка, вот тут мы реализуем его.
                    // navController.navigate(Screen.EditNote.route + "/${it.id}")
                    navController.navigate(Screen.EditNote.createRoute(it.id))//Тут it это обьект заметки Note
                    // эта строка то же самое что и navController.navigate("edit_note/$noteId")
                },
                onAddNoteClick = {
                    navController.navigate(Screen.CreateNote.route)
                }
            )
        }
        composable(Screen.CreateNote.route) {//если строка "create_note" то выполнится функция далее
            CreateNoteScreen(
                onFinished = {
                    navController.popBackStack()
                }
            )
        }
        composable(Screen.EditNote.route) {// если строка "edit_note/{note_id}" то выполнится функция далее

            val noteId = it.arguments?.getString("note_id")?.toInt() ?: 0// тут it это NavBackStackEntry а arguments один из его встроенных параметров.
            // val noteId = Screen.EditNote.getNoteId(it.arguments)
            EditNoteScreen(
                noteId = noteId,
                onFinished = {
                    navController.popBackStack()

                }
            )

        }
    }
}


sealed class Screen(val route: String) {
    data object Notes : Screen("notes")
    data object CreateNote : Screen("create_note")
    data object EditNote : Screen("edit_note/{note_id}") { //Bundle("note_id" - "5")

        fun createRoute(noteId: Int): String {//тут мы просто формируем строку подставляя наш айди
            return "edit_note/$noteId"
        }
//        fun getNoteId(arguments: Bundle?):Int{
//            return arguments?.getString("note_id")?.toInt() ?: 0
//        }
    }
}

