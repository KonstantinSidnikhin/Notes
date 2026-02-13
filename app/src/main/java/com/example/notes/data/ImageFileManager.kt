package com.example.notes.data

import android.content.Context
import androidx.core.net.toUri
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

import java.io.File
import java.util.UUID
import javax.inject.Inject

class ImageFileManager @Inject constructor(// Созданием экземпляра будет заниматься Hilt поэтому инжект
    @ApplicationContext private val context: Context
) {
    private val imagesDir: File = context.filesDir// ссылка на папку в internal storage
    suspend fun copyImageToInternalStorage(url: String): String {// функция берет урл из экстернал и сохраняет в интернал хранилище
        val fileName = "IMG_${UUID.randomUUID()}.jpg"// генерим рандомный айди
        val file = File(imagesDir, fileName)//создали файл во внутреннем хранилище

        withContext(Dispatchers.IO) {
            context.contentResolver.openInputStream(url.toUri())?.use { inputStream ->//этот файл на устройстве пользователя!
                // мы открываем поток передавая ему адрес файла в виде uri у нас урл в виде строки переводим в ури

                file.outputStream().use { outputStream ->//"use" under the hood it's try catch
                    inputStream.copyTo(outputStream)// открыли поток данных и скопировали из него в другой поток (output)
                }
            }
        }
        return file.absolutePath
    }

    suspend fun deleteImage(url: String) {
        withContext(Dispatchers.IO) {
            val file = File(url)// по данному пути(url), создаем экземпляр класса File
            if (file.exists()&& isInternal(file.absolutePath)){// удаляем если он есть во внутреннем хранилище
                file.delete()
            }
        }
    }

    fun isInternal(url: String): Boolean {
        return url.startsWith(imagesDir.absolutePath)
    }
}