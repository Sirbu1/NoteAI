package com.notai.ui

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.notai.R
import com.notai.data.model.NoteWithTags
import com.notai.databinding.ActivityMainBinding
import com.notai.ui.adapter.NoteAdapter
import com.notai.ui.note.NoteEditActivity
import com.notai.ui.tag.TagManageActivity
import com.notai.ui.viewmodel.NoteViewModel
import kotlinx.coroutines.launch

/**
 * 主界面 - 显示笔记列表
 */
class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    private val viewModel: NoteViewModel by viewModels { NoteViewModel.provideFactory() }
    private lateinit var noteAdapter: NoteAdapter
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupToolbar()
        setupRecyclerView()
        setupSearch()
        setupFab()
        observeViewModel()
    }
    
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
    }
    
    private fun setupRecyclerView() {
        noteAdapter = NoteAdapter(
            onNoteClick = { note ->
                openNoteEdit(note.note.id)
            },
            onNoteLongClick = { note ->
                showDeleteDialog(note.note.id)
            }
        )
        
        binding.notesRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = noteAdapter
        }
    }
    
    private fun setupSearch() {
        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.searchNotes(s?.toString() ?: "")
            }
            
            override fun afterTextChanged(s: Editable?) {}
        })
    }
    
    private fun setupFab() {
        binding.fabAddNote.setOnClickListener {
            openNoteEdit(null)
        }
    }
    
    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.notes.collect { notes ->
                noteAdapter.submitList(notes)
                binding.emptyTextView.visibility = if (notes.isEmpty()) View.VISIBLE else View.GONE
            }
        }
    }
    
    private fun openNoteEdit(noteId: Long?) {
        val intent = Intent(this, NoteEditActivity::class.java)
        if (noteId != null) {
            intent.putExtra(NoteEditActivity.EXTRA_NOTE_ID, noteId)
        }
        startActivity(intent)
    }
    
    private fun showDeleteDialog(noteId: Long) {
        AlertDialog.Builder(this)
            .setTitle(R.string.confirm_delete)
            .setMessage(R.string.confirm_delete_note)
            .setPositiveButton(R.string.delete) { _, _ ->
                viewModel.deleteNote(noteId)
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }
    
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_manage_tags -> {
                startActivity(Intent(this, TagManageActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}

