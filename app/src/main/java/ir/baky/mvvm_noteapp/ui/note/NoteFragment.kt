package ir.baky.mvvm_noteapp.ui.note

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import ir.baky.mvvm_noteapp.R
import ir.baky.mvvm_noteapp.data.model.NoteEntity
import ir.baky.mvvm_noteapp.databinding.FragmentNoteBinding
import ir.baky.mvvm_noteapp.utils.*
import ir.baky.mvvm_noteapp.utils.setupListWithAdapter
import ir.baky.mvvm_noteapp.viewmodel.NoteViewModel
import javax.inject.Inject

@AndroidEntryPoint
class NoteFragment : BottomSheetDialogFragment() {
    //Binding
    private var _binding: FragmentNoteBinding? = null
    private val binding get() = _binding

    @Inject
    lateinit var entity: NoteEntity

    //Other
    private val viewModel: NoteViewModel by viewModels()
    private var category = ""
    private var priority = ""
    private var noteId = 0
    private var type = ""
    private val categoriesList: MutableList<String> = mutableListOf()
    private val prioritiesList: MutableList<String> = mutableListOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentNoteBinding.inflate(layoutInflater)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Bundle
        noteId = arguments?.getInt(BUNDLE_ID) ?: 0
        //Type
        type = if (noteId>0){
            EDIT
        }else{
            NEW
        }
        //Init Views
        binding?.apply {
            //close
            closeImg.setOnClickListener {
                dismiss()
            }
            //Spinner Category
            viewModel.loadCategoriesData()
            viewModel.categoriesList.observe(viewLifecycleOwner){
                categoriesList.addAll(it)
                categoriesSpinner.setupListWithAdapter(it){ itItem ->
                    category = itItem
                }
            }
            //Spinner Priority
            viewModel.loadPrioritiesData()
            viewModel.prioritiesList.observe(viewLifecycleOwner){
                prioritiesList.addAll(it)
                prioritySpinner.setupListWithAdapter(it){ itItem ->
                    priority = itItem
                }
            }
            //Note data
            if(type == EDIT){
                saveNote.setText(getText(R.string.update))
                viewModel.getData(noteId)
                viewModel.noteData.observe(viewLifecycleOwner){
                    it.data?.let { data ->
                        titleEdt.setText(data.title)
                        descEdt.setText(data.desc)
                        categoriesSpinner.setSelection(categoriesList.getIndexFromList(data.category), true)
                        prioritySpinner.setSelection(prioritiesList.getIndexFromList(data.priority), true)
                    }
                }
            }
            //Click
            saveNote.setOnClickListener {
                val title = titleEdt.text.toString()
                val desc = descEdt.text.toString()
                entity.id = noteId
                entity.title = title
                entity.desc = desc
                entity.category = category
                entity.priority = priority

                if (title.isEmpty() || desc.isEmpty()) {
                    Toast.makeText(requireContext(), "Title/Description can not be empty!", Toast.LENGTH_LONG).show()
                } else {
                    if (type == NEW){
                        //Save
                        viewModel.saveEditeNote(false, entity)
                    }else{
                        //Edit
                        viewModel.saveEditeNote(true, entity)
                    }
                    //closing the fragment
                    dismiss()
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        _binding = null
    }
}