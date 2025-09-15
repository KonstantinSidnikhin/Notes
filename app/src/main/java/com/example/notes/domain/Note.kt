package com.example.notes.domain

data class Note(
    val id: Int,
    val title: String,
    val content: String,
    val updated:Long,
    val isPinned: Boolean
)
