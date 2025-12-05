package com.example.notes.data

import com.example.notes.domain.Note

fun Note.toDbModel(): NoteDbModel{// мапер преобразовывает модель дата слоя в модель домэйн слоя и обратно
    return NoteDbModel(id,title,content,updatedAt,isPinned)
}
fun NoteDbModel.toEntity():Note{
    return Note(id,title,content,updatedAt,isPinned)
}