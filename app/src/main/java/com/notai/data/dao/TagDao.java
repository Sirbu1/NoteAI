package com.notai.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.*;

import com.notai.data.model.NoteTagCrossRef;
import com.notai.data.model.Tag;

import java.util.List;

/**
 * 标签数据访问对象
 */
@Dao
public interface TagDao {
    
    @Query("SELECT * FROM tags ORDER BY name ASC")
    LiveData<List<Tag>> getAllTags();
    
    @Query("SELECT * FROM tags WHERE id = :tagId")
    Tag getTagById(long tagId);
    
    @Query("SELECT * FROM tags WHERE name = :name")
    Tag getTagByName(String name);
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertTag(Tag tag);
    
    @Update
    void updateTag(Tag tag);
    
    @Delete
    void deleteTag(Tag tag);
    
    @Query("DELETE FROM tags WHERE id = :tagId")
    void deleteTagById(long tagId);
    
    // 笔记标签关联操作
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertNoteTagCrossRef(NoteTagCrossRef crossRef);
    
    @Delete
    void deleteNoteTagCrossRef(NoteTagCrossRef crossRef);
    
    @Query("DELETE FROM note_tag_cross_ref WHERE noteId = :noteId")
    void deleteNoteTagRefsByNoteId(long noteId);
    
    @Query("DELETE FROM note_tag_cross_ref WHERE tagId = :tagId")
    void deleteNoteTagRefsByTagId(long tagId);
    
    @Query("SELECT * FROM note_tag_cross_ref WHERE noteId = :noteId")
    List<NoteTagCrossRef> getTagsByNoteId(long noteId);
}

