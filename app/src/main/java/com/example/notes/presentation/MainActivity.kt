package com.example.notes.presentation

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.notes.presentation.screens.creation.CreateNoteScreen
import com.example.notes.presentation.screens.editing.EditNoteScreen
import com.example.notes.presentation.screens.notes.NotesScreen
import com.example.notes.presentation.ui.theme.NotesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NotesTheme {

                    EditNoteScreen(
                        noteId = 5,
                        onFinished = {
                        Log.d("CreateNoteScreen","onFinished")
                   }
                    )
      //          CreateNoteScreen(
  //                  onFinished = {
    //                    Log.d("CreateNoteScreen","onFinished")
      //             }
        //        )
//                NotesScreen(
//                    onNoteClick1 = {// в функции NotesScreen у нас был плэйс холдер для коллбэка, вот тут мы реализуем его.
//                        Log.d("MainActivity","onNoteClick: $it")
//                    },
//                    onAddNoteClick = {
//                        Log.d("MainActivity","onAddNoteClick")
//                    }
//                )


            }
        }
    }

}

