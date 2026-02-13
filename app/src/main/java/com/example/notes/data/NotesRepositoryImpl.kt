package com.example.notes.data

import android.content.Context
import com.example.notes.domain.ContentItem
import com.example.notes.domain.Note
import com.example.notes.domain.NotesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class NotesRepositoryImpl @Inject constructor(
    private val notesDao: NotesDao,
    private val imageFileManager: ImageFileManager

) : NotesRepository {

    override suspend fun addNote(
        title: String,
        content: List<ContentItem>,
        isPinned: Boolean,
        updatedAt: Long
    ) {
        val note = Note(0, title, content.processForStorage(), updatedAt, isPinned)
        val noteDbModel = note.toDbModel()
        notesDao.addNote(noteDbModel)
    }


    override suspend fun deleteNote(noteId: Int) {//если мы удалили заметку с экрана, то и все изображения
        // из нее должны быть удалены из внутреннего хранилища
        val note = notesDao.getNote(noteId).toEntity()
        notesDao.deleteNote(noteId)
        note.content//получаем коллекцию с адресами изображений
            .filterIsInstance<ContentItem.Image>()
            .map { it.url }
            .forEach {
                imageFileManager.deleteImage(it)//и для каждого изображения вызываем delete
            }
    }

    override suspend fun editNote(note: Note) {
        val oldNote = notesDao.getNote(note.id)//получаем старую заметку из базы с тем же айди
            // что и редактируем, что бы сравнить изображения из старой и из новой
            .toEntity()//Берем старую заметку из базы и Приводим к модели домэйн слоя

        val oldUrls =
            oldNote.content.filterIsInstance<ContentItem.Image>()//получаем адреса всех изображений из старой заметки
                .map { it.url }//проходим по всему контенту фильтруем
        // оставляя картинки и на выходе оставляем коллекцию обьектов String с адресами всех изображений

        val newUrls = note.content.filterIsInstance<ContentItem.Image>()
            .map { it.url }//получаем адреса изображений из новой заметки

        val removedUrls =
            oldUrls - newUrls//если какие то картинки были в старой коллекции а в новой их нет значит мы их удалили
        // с экрана и нам надо их удалить и из внутреннего хранилища, получим удаленные адреса

        removedUrls.forEach { imageFileManager.deleteImage(it) }// у каждого удаленного изображения вызваем fun deleteImage
        val processedContent =
            note.content.processForStorage()// все новые изображения сохраняем во внутреннее хранилище. Если ничего не добавлено он ничего не сделает

        val processedNote =
            note.copy(content = processedContent)//обьект с корректными адресами изображений

        notesDao.addNote(processedNote.toDbModel())//сохраняем заметку в базу данных приведя к типу NoteDbModel

    }

    override fun getAllNotes(): Flow<List<Note>> {
        return notesDao.getAllNotes().map {
            it.toEntities()
        }
    }

    override suspend fun getNote(noteId: Int): Note {
        return notesDao.getNote(noteId).toEntity()
    }

    override fun searchNotes(query: String): Flow<List<Note>> {
        return notesDao.searchNotes(query).map { it.toEntities() }
    }

    override suspend fun switchPinnedStatus(noteId: Int) {
        notesDao.switchPinnedStatus(noteId)

    }

    private suspend fun List<ContentItem>.processForStorage(): List<ContentItem> {
        return map { contentItem ->
            when (contentItem) {
                is ContentItem.Image -> {
                    if (imageFileManager.isInternal(contentItem.url)) {
                        contentItem
                    } else {
                        val internalPath =
                            imageFileManager.copyImageToInternalStorage(contentItem.url)//передаем
                        // адрес изображения и новый адрес сохраним в переменную
                        ContentItem.Image(internalPath)//вернем новый обьект с обновленным путем
                        // к изображению. Теперь адрес из внутреннего хранилища
                    }
                }

                is ContentItem.Text -> contentItem
            }
        }
    }
}