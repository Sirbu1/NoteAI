package com.notai.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.notai.NoteAIApplication;
import com.notai.data.model.Note;
import com.notai.data.model.NoteWithTags;
import com.notai.data.repository.NoteRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * 笔记 ViewModel
 */
public class NoteViewModel extends ViewModel {
    
    private final NoteRepository noteRepository;
    private final MutableLiveData<String> searchQuery = new MutableLiveData<>("");
    private final LiveData<List<NoteWithTags>> notes;
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    
    public NoteViewModel(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
        
        // 使用 Transformations.switchMap 根据搜索查询切换数据源
        notes = Transformations.switchMap(searchQuery, query -> {
            if (query == null || query.trim().isEmpty()) {
                return noteRepository.getAllNotesWithTags();
            } else {
                return noteRepository.searchNotesWithTags(query);
            }
        });
    }
    
    public LiveData<List<NoteWithTags>> getNotes() {
        return notes;
    }
    
    public LiveData<String> getSearchQuery() {
        return searchQuery;
    }
    
    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }
    
    public void searchNotes(String query) {
        searchQuery.setValue(query != null ? query : "");
    }
    
    public void insertNote(Note note, List<Long> tagIds) {
        isLoading.setValue(true);
        noteRepository.insertNote(note, tagIds != null ? tagIds : new ArrayList<>(), noteId -> {
            isLoading.setValue(false);
        });
    }
    
    public void updateNote(Note note, List<Long> tagIds) {
        isLoading.setValue(true);
        note.setUpdatedAt(System.currentTimeMillis());
        noteRepository.updateNote(note, tagIds != null ? tagIds : new ArrayList<>());
        isLoading.setValue(false);
    }
    
    public void deleteNote(long noteId) {
        isLoading.setValue(true);
        noteRepository.deleteNote(noteId);
        isLoading.setValue(false);
    }
    
    public NoteWithTags getNoteWithTagsById(long noteId) {
        return noteRepository.getNoteWithTagsById(noteId);
    }
    
    public static class Factory implements ViewModelProvider.Factory {
        @Override
        @SuppressWarnings("unchecked")
        public <T extends ViewModel> T create(Class<T> modelClass) {
            if (modelClass.isAssignableFrom(NoteViewModel.class)) {
                NoteAIApplication application = NoteAIApplication.getInstance();
                return (T) new NoteViewModel(application.getNoteRepository());
            }
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}

