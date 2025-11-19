package com.notai.data.repository;

import androidx.lifecycle.LiveData;

import com.notai.data.dao.TagDao;
import com.notai.data.model.Tag;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * 标签仓库类（数据层）
 */
public class TagRepository {
    private final TagDao tagDao;
    private final Executor executor;

    public TagRepository(TagDao tagDao) {
        this.tagDao = tagDao;
        this.executor = Executors.newSingleThreadExecutor();
    }

    public LiveData<List<Tag>> getAllTags() {
        return tagDao.getAllTags();
    }

    public Tag getTagById(long tagId) {
        return tagDao.getTagById(tagId);
    }

    public Tag getTagByName(String name) {
        return tagDao.getTagByName(name);
    }

    public void insertTag(Tag tag) {
        executor.execute(() -> tagDao.insertTag(tag));
    }

    public void updateTag(Tag tag) {
        executor.execute(() -> tagDao.updateTag(tag));
    }

    public void deleteTag(long tagId) {
        executor.execute(() -> {
            // 先删除关联关系
            tagDao.deleteNoteTagRefsByTagId(tagId);
            // 再删除标签
            tagDao.deleteTagById(tagId);
        });
    }
}

