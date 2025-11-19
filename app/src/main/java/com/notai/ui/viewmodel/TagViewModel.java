package com.notai.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.notai.NoteAIApplication;
import com.notai.data.model.Tag;
import com.notai.data.repository.TagRepository;

import java.util.List;

/**
 * 标签 ViewModel
 */
public class TagViewModel extends ViewModel {
    
    private final TagRepository tagRepository;
    private final LiveData<List<Tag>> tags;
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    
    public TagViewModel(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
        this.tags = tagRepository.getAllTags();
    }
    
    public LiveData<List<Tag>> getTags() {
        return tags;
    }
    
    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }
    
    public void insertTag(Tag tag) {
        isLoading.postValue(true);
        tagRepository.insertTag(tag);
        // 插入操作是异步的，但由于 Room 的 LiveData 会自动更新，我们可以立即设置为 false
        isLoading.postValue(false);
    }
    
    public void updateTag(Tag tag) {
        isLoading.postValue(true);
        tagRepository.updateTag(tag);
        // 更新操作是异步的，但由于 Room 的 LiveData 会自动更新，我们可以立即设置为 false
        isLoading.postValue(false);
    }
    
    public void deleteTag(long tagId) {
        isLoading.postValue(true);
        tagRepository.deleteTag(tagId);
        // 删除操作是异步的，但由于 Room 的 LiveData 会自动更新，我们可以立即设置为 false
        isLoading.postValue(false);
    }
    
    public static class Factory implements ViewModelProvider.Factory {
        @Override
        @SuppressWarnings("unchecked")
        public <T extends ViewModel> T create(Class<T> modelClass) {
            if (modelClass.isAssignableFrom(TagViewModel.class)) {
                NoteAIApplication application = NoteAIApplication.getInstance();
                return (T) new TagViewModel(application.getTagRepository());
            }
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}

