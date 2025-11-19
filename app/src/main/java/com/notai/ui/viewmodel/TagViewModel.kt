package com.notai.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.notai.NoteAIApplication
import com.notai.data.model.Tag
import com.notai.data.repository.TagRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * 标签 ViewModel
 */
class TagViewModel(
    private val tagRepository: TagRepository
) : ViewModel() {
    
    private val _tags = MutableStateFlow<List<Tag>>(emptyList())
    val tags: StateFlow<List<Tag>> = _tags.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    init {
        loadTags()
    }
    
    fun loadTags() {
        viewModelScope.launch {
            tagRepository.getAllTags().collect { tagsList ->
                _tags.value = tagsList
            }
        }
    }
    
    fun insertTag(tag: Tag) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                tagRepository.insertTag(tag)
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun updateTag(tag: Tag) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                tagRepository.updateTag(tag)
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun deleteTag(tagId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                tagRepository.deleteTag(tagId)
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    companion object {
        fun provideFactory(): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    val application = NoteAIApplication.getInstance()
                    return TagViewModel(application.tagRepository) as T
                }
            }
        }
    }
}

