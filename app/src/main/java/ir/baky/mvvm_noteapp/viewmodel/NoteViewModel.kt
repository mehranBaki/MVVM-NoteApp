package ir.baky.mvvm_noteapp.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.baky.mvvm_noteapp.data.model.NoteEntity
import ir.baky.mvvm_noteapp.data.repository.NoteRepository
import ir.baky.mvvm_noteapp.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(private val repository: NoteRepository):ViewModel(){
    //Spinners
    private var _categoriesList: MutableLiveData<MutableList<String>> = MutableLiveData()
    val categoriesList: LiveData<MutableList<String>> = _categoriesList
    private var _prioritiesList: MutableLiveData<MutableList<String>> = MutableLiveData()
    val prioritiesList: LiveData<MutableList<String>> = _prioritiesList

    //Data
    private var _noteData: MutableLiveData<DataStatus<NoteEntity>> = MutableLiveData()
    val noteData: LiveData<DataStatus<NoteEntity>> = _noteData

    fun loadCategoriesData() = viewModelScope.launch(Dispatchers.IO) {
        val data = mutableListOf(WORK, EDUCATION, HOME, HEALTH)
        _categoriesList.postValue(data)
        Log.e("LOG:","NoteViewModel-loadCategoriesData")
    }

    fun loadPrioritiesData() = viewModelScope.launch(Dispatchers.IO) {
        val data = mutableListOf(HIGH, NORMAL, LOW)
        _prioritiesList.postValue(data)
        Log.e("LOG:","NoteViewModel-loadPrioritiesData")
    }

    fun saveEditeNote(isEdit: Boolean, entity: NoteEntity) = viewModelScope.launch(Dispatchers.IO){
        if(isEdit){
            //Edit
            repository.editNote(entity)
            Log.e("LOG:","NoteViewModel-EDITE")
        }else{
            //Save
            repository.saveNote(entity)
            Log.e("LOG:","NoteViewModel-SAVE")
        }
    }

    fun getData(id: Int) = viewModelScope.launch(Dispatchers.IO) {
        repository.getNote(id).collect{
            _noteData.postValue(DataStatus.success(it, false))
            Log.e("LOG:","NoteViewModel-getData")
        }
    }
}