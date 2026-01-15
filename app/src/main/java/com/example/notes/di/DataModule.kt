package com.example.notes.di

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import com.example.notes.data.NotesDao
import com.example.notes.data.NotesDataBase
import com.example.notes.data.NotesRepositoryImpl
import com.example.notes.domain.NotesRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {
    @Singleton
    @Binds
    fun bindRepository(//связываем репо в домэйне и репо в дате
        impl: NotesRepositoryImpl
    ): NotesRepository

    companion object {
        @Singleton
        @Provides
        fun provideDatabase(
            @ApplicationContext context: Context
        ): NotesDataBase {//для абстрактного класса БД пишем провайд метод
            //return NotesDataBase.getInstance(context)//Если мы написали провайд метод значит мы положили эту зависимость в Компонент, и далее можем ее использовать

            return Room.databaseBuilder(
                context = context,
                klass = NotesDataBase::class.java,
                name = "notes.db"
            ).fallbackToDestructiveMigration(dropAllTables = true).build()
        }

        @Singleton
        @Provides
        fun provideNotesDao(database: NotesDataBase): NotesDao {//NotesDao это интерфейс, поэтому @Inject не можем навесить на него, пишем провайд метод
            return database.notesDao()
        }

    }
}