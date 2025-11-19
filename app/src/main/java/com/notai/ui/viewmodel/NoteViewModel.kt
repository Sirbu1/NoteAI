package com.notai.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.notai.NoteAIApplication
import com.notai.data.model.Note
import com.notai.data.model.NoteWithTags
import com.notai.data.repository.NoteRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * 笔记 ViewModel
 */
class NoteViewModel(
    private val noteRepository: NoteRepository
) : ViewModel() {
    
    private val _notes = MutableStateFlow<List<NoteWithTags>>(emptyList())
    val notes: StateFlow<List<NoteWithTags>> = _notes.asStateFlow()
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private var searchJob: Job? = null
    
    init {
        loadNotes()
    }
    
    fun loadNotes() {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            noteRepository.getAllNotesWithTags().collect { notesList ->
                _notes.value = notesList
            }
        }
    }
    
    fun searchNotes(query: String) {
        _searchQuery.value = query
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            if (query.isBlank()) {
                noteRepository.getAllNotesWithTags().collect { notesList ->
                    _notes.value = notesList
                }
            } else {
                noteRepository.searchNotesWithTags(query).collect { notesList ->
                    _notes.value = notesList
                }
            }
        }
    }
    
    fun insertNote(note: Note, tagIds: List<Long> = emptyList()) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                noteRepository.insertNote(note, tagIds)
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun updateNote(note: Note, tagIds: List<Long> = emptyList()) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val updatedNote = note.copy(updatedAt = System.currentTimeMillis())
                noteRepository.updateNote(updatedNote, tagIds)
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun deleteNote(noteId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                noteRepository.deleteNote(noteId)
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    suspend fun getNoteWithTagsById(noteId: Long): NoteWithTags? {
        return noteRepository.getNoteWithTagsById(noteId)
    }
    
    companion object {
        fun provideFactory(): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    val application = NoteAIApplication.getInstance()
                    return NoteViewModel(application.noteRepository) as T
                }
            }
        }
    }
}

