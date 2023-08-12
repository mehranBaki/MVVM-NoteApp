package ir.baky.mvvm_noteapp.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.baky.mvvm_noteapp.data.model.NoteEntity
import ir.baky.mvvm_noteapp.data.repository.MainRepository
import ir.baky.mvvm_noteapp.utils.DataStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val repository: MainRepository) : ViewModel() {

    private var _notesData: MutableLiveData<DataStatus<List<NoteEntity>>> = MutableLiveData()
    val notesData: LiveData<DataStatus<List<NoteEntity>>> = _notesData

    private var _searchData: MutableLiveData<DataStatus<List<NoteEntity>>> = MutableLiveData()
    val searchData: LiveData<DataStatus<List<NoteEntity>>> = _searchData

    private var _filterData: MutableLiveData<DataStatus<List<NoteEntity>>> = MutableLiveData()
    val filterData: LiveData<DataStatus<List<NoteEntity>>> = _filterData

    private var _isSearch: MutableLiveData<Boolean> = MutableLiveData()
    val isSearch = _isSearch

    private var _isFilter: MutableLiveData<Boolean> = MutableLiveData()
    val isFilter = _isFilter

    fun getAllNote() = viewModelScope.launch(Dispatchers.IO) {
        repository.allNotes().collect {
            _notesData.postValue(DataStatus.success(it, it.isEmpty()))
            Log.e("LOG:", "MainViewModel-getAllNote")
        }
    }

    fun getSearchNote(search: String) = viewModelScope.launch(Dispatchers.IO) {
        _isSearch.postValue(true)
        repository.searchNotes(search).collect {
            _searchData.postValue(DataStatus.success(it, it.isEmpty()))
            Log.e("LOG:", "MainViewModel-getSearchNote")
            _isSearch.postValue(false)
        }
    }

    fun getFilterNote(filter: String) = viewModelScope.launch(Dispatchers.IO) {
        _isFilter.postValue(true)
        repository.filterNotes(filter).collect {
            _filterData.postValue(DataStatus.success(it, it.isEmpty()))
            Log.e("LOG:", "MainViewModel-getFilterNote")
            _isFilter.postValue(false)
        }
    }

    fun deleteNote(entity: NoteEntity) = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteNote(entity)
        Log.e("LOG:", "MainViewModel-deleteNote")
    }

    fun getSearchNoteWithPriority(search: String, filter: String) = viewModelScope.launch(Dispatchers.IO) {
            _isSearch.postValue(true)
            repository.searchNotesWithPriority(search, filter).collect {
                _searchData.postValue(DataStatus.success(it, it.isEmpty()))
                Log.e("LOG:", "MainViewModel-getSearchNoteWithPriority")
                _isSearch.postValue(false)
            }
        }
}