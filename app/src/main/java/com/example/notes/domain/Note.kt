package com.example.notes.domain


//@Entity// мы удаляем все что связано с дата слоем из домэйн слоя
data class Note(
    //@PrimaryKey(autoGenerate = true)// мы удаляем все что связано с дата слоем из домэйн слоя
    val id: Int,
    val title: String,
    val content: List<ContentItem>,
    val updatedAt: Long,
    val isPinned: Boolean
)