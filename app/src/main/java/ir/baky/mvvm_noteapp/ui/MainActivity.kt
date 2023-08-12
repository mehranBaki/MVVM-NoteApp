package ir.baky.mvvm_noteapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import ir.baky.mvvm_noteapp.R
import ir.baky.mvvm_noteapp.data.model.NoteEntity
import ir.baky.mvvm_noteapp.databinding.ActivityMainBinding
import ir.baky.mvvm_noteapp.ui.note.NoteFragment
import ir.baky.mvvm_noteapp.utils.*
import ir.baky.mvvm_noteapp.viewmodel.MainViewModel
import kotlinx.coroutines.*
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    //Binding
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding

    @Inject
    lateinit var notesAdapter: NoteAdapter

    @Inject
    lateinit var noteEntity: NoteEntity

    //Other
    private val viewModel: MainViewModel by viewModels()
    private var selectedItem = 0
    private var isFiltered = false
    private var filteredItem = ""

    //Jobs
    private var allNoteJob: Job? = null
    private var searchJob: Job? = null
    private var filterJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        //For day mode theme only
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        //Init Views
        binding?.apply {
            //support toolbar
            setSupportActionBar(notesToolbar)
            //Note Fragment
            addNoteBtn.setOnClickListener {
                NoteFragment().show(supportFragmentManager, NoteFragment().tag)
            }
            //Get All data
            allNoteJob = viewModel.getAllNote()
            viewModel.notesData.observe(this@MainActivity) {
                if (isFiltered) {
                    filterJob = viewModel.getFilterNote(filteredItem)
                } else {
                    showEmpty(it.isEmpty)
                    notesAdapter.setData(it.data!!)
                    noteList.apply {
                        layoutManager =
                            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
                        adapter = notesAdapter
                    }
                }
            }
            //search observation
            viewModel.searchData.observe(this@MainActivity) {
                showEmpty(it.isEmpty)
                notesAdapter.setData(it.data!!)
                noteList.apply {
                    layoutManager =
                        StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
                    adapter = notesAdapter
                }
            }
            //canceling the search coroutine
            viewModel.isSearch.observe(this@MainActivity) {
                if (!it) {
                    searchJob?.cancel()
                }
            }
            //filter observation
            viewModel.filterData.observe(this@MainActivity) {
                showEmpty(it.isEmpty)
                notesAdapter.setData(it.data!!)
                noteList.apply {
                    layoutManager =
                        StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
                    adapter = notesAdapter
                }
            }
            //canceling the Filter coroutine
            viewModel.isFilter.observe(this@MainActivity) {
                if (!it) {
                    filterJob?.cancel()
                }
            }
            //Filter
            notesToolbar.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.actionFilter -> {
                        filterByPriority()
                        return@setOnMenuItemClickListener true
                    }
                    else -> {
                        return@setOnMenuItemClickListener false
                    }
                }
            }
            //Note's menu clicks
            notesAdapter.setOnItemClickListener { entity, type ->
                when (type) {
                    EDIT -> {
                        val noteFragment = NoteFragment()
                        val bundle = Bundle()
                        bundle.putInt(BUNDLE_ID, entity.id)
                        noteFragment.arguments = bundle
                        noteFragment.show(supportFragmentManager, NoteFragment().tag)
                    }
                    DELETE -> {
                        noteEntity.id = entity.id
                        noteEntity.title = entity.title
                        noteEntity.desc = entity.desc
                        noteEntity.category = entity.category
                        noteEntity.priority = entity.priority
                        viewModel.deleteNote(noteEntity)
                    }
                }
            }
        }
    }

    private fun showEmpty(isShown: Boolean) {
        binding?.apply {
            if (isShown) {
                emptyLay.visibility = View.VISIBLE
                noteList.visibility = View.GONE
            } else {
                emptyLay.visibility = View.GONE
                noteList.visibility = View.VISIBLE
            }
        }
    }


    private fun filterByPriority() {
        val builder = AlertDialog.Builder(this)
        val priorities = arrayOf(ALL, HIGH, NORMAL, LOW)
        builder.setSingleChoiceItems(priorities, selectedItem) { dialog, item ->
            when (item) {
                0 -> {
                    isFiltered = false
                    allNoteJob = viewModel.getAllNote()
                }
                in 1..3 -> {
                    isFiltered = true
                    filteredItem = priorities[item]
                    filterJob = viewModel.getFilterNote(priorities[item])
                }
            }
            selectedItem = item
            dialog.dismiss()
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar, menu)
        val search = menu.findItem(R.id.actionSearch)
        val searchView = search.actionView as SearchView
        searchView.queryHint = getString(R.string.search)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                if (isFiltered) {
                    //Search using the filter
                    if(newText.isEmpty()){
                        filterJob = viewModel.getFilterNote(filteredItem)
                    }else{
                        searchJob = viewModel.getSearchNoteWithPriority(newText, filteredItem)
                    }
                } else {
                    //Search
                    searchJob = viewModel.getSearchNote(newText)
                }
                return true
            }

        })

        return super.onCreateOptionsMenu(menu)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        allNoteJob?.cancel()
        searchJob?.cancel()
        filterJob?.cancel()
    }

}