package com.example.notes.domain

//import androidx.room.Entity// мы удаляем все что связано с дата слоем из домэйн слоя
//import androidx.room.PrimaryKey// мы удаляем все что связано с дата слоем из домэйн слоя

//@Entity// мы удаляем все что связано с дата слоем из домэйн слоя
data class Note(
    //@PrimaryKey(autoGenerate = true)// мы удаляем все что связано с дата слоем из домэйн слоя
    val id: Int,
    val title: String,
    val content: String,
    val updatedAt:Long,
    val isPinned: Boolean
)
