package ir.baky.mvvm_noteapp.data.repository

import ir.baky.mvvm_noteapp.data.database.NoteDao
import ir.baky.mvvm_noteapp.data.model.NoteEntity
import javax.inject.Inject

class MainRepository @Inject constructor(private val dao: NoteDao){
    fun allNotes() = dao.getAllNotes()
    fun searchNotes(search: String) = dao.searchNote(search)
    fun filterNotes(filter: String) = dao.filterNote(filter)
    suspend fun deleteNote(entity: NoteEntity) = dao.deleteNote(entity)
    fun searchNotesWithPriority(search: String, filter: String) = dao.searchNoteWithPriority(search, filter)

}