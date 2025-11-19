package com.notai.ui.viewmodel;

import android.util.Log;

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
    
    private static final String TAG = "NoteViewModel";
    
    private final NoteRepository noteRepository;
    private final MutableLiveData<String> searchQuery = new MutableLiveData<>("");
    private final LiveData<List<NoteWithTags>> notes;
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    
    public NoteViewModel(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
        
        try {
            // 使用 Transformations.switchMap 根据搜索查询切换数据源
            notes = Transformations.switchMap(searchQuery, query -> {
                if (query == null || query.trim().isEmpty()) {
                    return noteRepository.getAllNotesWithTags();
                } else {
                    return noteRepository.searchNotesWithTags(query);
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error initializing NoteViewModel", e);
            throw e;
        }
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
        isLoading.postValue(true);
        noteRepository.insertNote(note, tagIds != null ? tagIds : new ArrayList<>(), noteId -> {
            isLoading.postValue(false);
        });
    }
    
    public void updateNote(Note note, List<Long> tagIds) {
        isLoading.postValue(true);
        note.setUpdatedAt(System.currentTimeMillis());
        noteRepository.updateNote(note, tagIds != null ? tagIds : new ArrayList<>());
        // 更新操作是异步的，但由于没有回调，我们延迟一点再设置为 false
        // 实际上，由于 Room 的 LiveData 会自动更新，我们可以立即设置为 false
        isLoading.postValue(false);
    }
    
    public void deleteNote(long noteId) {
        isLoading.postValue(true);
        noteRepository.deleteNote(noteId);
        // 删除操作是异步的，但由于没有回调，我们延迟一点再设置为 false
        // 实际上，由于 Room 的 LiveData 会自动更新，我们可以立即设置为 false
        isLoading.postValue(false);
    }
    
    public LiveData<NoteWithTags> getNoteWithTagsById(long noteId) {
        return noteRepository.getNoteWithTagsById(noteId);
    }
    
    public static class Factory implements ViewModelProvider.Factory {
        @Override
        @SuppressWarnings("unchecked")
        public <T extends ViewModel> T create(Class<T> modelClass) {
            if (modelClass.isAssignableFrom(NoteViewModel.class)) {
                try {
                    NoteAIApplication application = NoteAIApplication.getInstance();
                    return (T) new NoteViewModel(application.getNoteRepository());
                } catch (Exception e) {
                    Log.e(TAG, "Error creating NoteViewModel", e);
                    throw new RuntimeException("Failed to create NoteViewModel", e);
                }
            }
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}

