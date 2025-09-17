package com.example.notes.domain

data class Note(
    val id: Int,
    val title: String,
    val content: String,
    val updatedAt:Long,
    val isPinned: Boolean
)
