package ir.baky.mvvm_noteapp.data.repository

import ir.baky.mvvm_noteapp.data.database.NoteDao
import ir.baky.mvvm_noteapp.data.model.NoteEntity
import javax.inject.Inject

class NoteRepository @Inject constructor(private val dao: NoteDao){
    suspend fun saveNote(entity: NoteEntity) = dao.saveNote(entity)
    suspend fun editNote(entity: NoteEntity) = dao.updateNote(entity)
    fun getNote(id: Int) = dao.getNote(id)
}