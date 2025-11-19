package com.notai.ui.note

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.notai.NoteAIApplication
import com.notai.R
import com.notai.data.model.Note
import com.notai.databinding.ActivityNoteEditBinding
import com.notai.ui.adapter.TagChipAdapter
import com.notai.ui.viewmodel.NoteViewModel
import com.notai.ui.viewmodel.TagViewModel
import kotlinx.coroutines.launch

/**
 * 笔记编辑界面
 */
class NoteEditActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityNoteEditBinding
    private val noteViewModel: NoteViewModel by viewModels { NoteViewModel.provideFactory() }
    private val tagViewModel: TagViewModel by viewModels { TagViewModel.provideFactory() }
    
    private var noteId: Long? = null
    private var selectedTagIds = mutableSetOf<Long>()
    private lateinit var tagChipAdapter: TagChipAdapter
    
    companion object {
        const val EXTRA_NOTE_ID = "extra_note_id"
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNoteEditBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        noteId = intent.getLongExtra(EXTRA_NOTE_ID, -1).takeIf { it != -1L }
        
        setupToolbar()
        setupTagRecyclerView()
        setupListeners()
        loadNote()
        loadTags()
    }
    
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = if (noteId != null) {
            getString(R.string.edit_note)
        } else {
            getString(R.string.add_note)
        }
    }
    
    private fun setupTagRecyclerView() {
        tagChipAdapter = TagChipAdapter(
            onTagSelected = { tagId, selected ->
                if (selected) {
                    selectedTagIds.add(tagId)
                } else {
                    selectedTagIds.remove(tagId)
                }
            }
        )
        
        binding.tagsRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@NoteEditActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = tagChipAdapter
        }
    }
    
    private fun setupListeners() {
        binding.addTagButton.setOnClickListener {
            showAddTagDialog()
        }
        
        binding.saveFab.setOnClickListener {
            saveNote()
        }
    }
    
    private fun loadNote() {
        if (noteId != null) {
            lifecycleScope.launch {
                val noteWithTags = noteViewModel.getNoteWithTagsById(noteId!!)
                noteWithTags?.let {
                    binding.titleEditText.setText(it.note.title)
                    binding.contentEditText.setText(it.note.content)
                    selectedTagIds = it.tags.map { tag -> tag.id }.toMutableSet()
                    updateTagSelection()
                }
            }
        }
    }
    
    private fun loadTags() {
        lifecycleScope.launch {
            tagViewModel.tags.collect { tags ->
                tagChipAdapter.submitList(tags)
                updateTagSelection()
            }
        }
    }
    
    private fun updateTagSelection() {
        if (::tagChipAdapter.isInitialized) {
            tagChipAdapter.setSelectedTagIds(selectedTagIds)
        }
    }
    
    private fun showAddTagDialog() {
        val input = android.widget.EditText(this)
        input.hint = getString(R.string.tag_name)
        
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle(R.string.add_tag)
            .setView(input)
            .setPositiveButton(R.string.save) { _, _ ->
                val tagName = input.text.toString().trim()
                if (tagName.isNotEmpty()) {
                    val tag = com.notai.data.model.Tag(name = tagName)
                    tagViewModel.insertTag(tag)
                } else {
                    Toast.makeText(this, R.string.empty_title, Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }
    
    private fun saveNote() {
        val title = binding.titleEditText.text.toString().trim()
        val content = binding.contentEditText.text.toString().trim()
        
        if (title.isEmpty()) {
            Toast.makeText(this, R.string.empty_title, Toast.LENGTH_SHORT).show()
            return
        }
        
        if (content.isEmpty()) {
            Toast.makeText(this, R.string.empty_content, Toast.LENGTH_SHORT).show()
            return
        }
        
        val note = if (noteId != null) {
            Note(
                id = noteId!!,
                title = title,
                content = content,
                updatedAt = System.currentTimeMillis()
            )
        } else {
            Note(
                title = title,
                content = content
            )
        }
        
        if (noteId != null) {
            noteViewModel.updateNote(note, selectedTagIds.toList())
        } else {
            noteViewModel.insertNote(note, selectedTagIds.toList())
        }
        
        finish()
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}

