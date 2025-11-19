package com.notai.data.dao

import androidx.room.*
import com.notai.data.model.NoteTagCrossRef
import com.notai.data.model.Tag
import kotlinx.coroutines.flow.Flow

/**
 * 标签数据访问对象
 */
@Dao
interface TagDao {
    
    @Query("SELECT * FROM tags ORDER BY name ASC")
    fun getAllTags(): Flow<List<Tag>>
    
    @Query("SELECT * FROM tags WHERE id = :tagId")
    suspend fun getTagById(tagId: Long): Tag?
    
    @Query("SELECT * FROM tags WHERE name = :name")
    suspend fun getTagByName(name: String): Tag?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTag(tag: Tag): Long
    
    @Update
    suspend fun updateTag(tag: Tag)
    
    @Delete
    suspend fun deleteTag(tag: Tag)
    
    @Query("DELETE FROM tags WHERE id = :tagId")
    suspend fun deleteTagById(tagId: Long)
    
    // 笔记标签关联操作
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertNoteTagCrossRef(crossRef: NoteTagCrossRef)
    
    @Delete
    suspend fun deleteNoteTagCrossRef(crossRef: NoteTagCrossRef)
    
    @Query("DELETE FROM note_tag_cross_ref WHERE noteId = :noteId")
    suspend fun deleteNoteTagRefsByNoteId(noteId: Long)
    
    @Query("DELETE FROM note_tag_cross_ref WHERE tagId = :tagId")
    suspend fun deleteNoteTagRefsByTagId(tagId: Long)
    
    @Query("SELECT * FROM note_tag_cross_ref WHERE noteId = :noteId")
    suspend fun getTagsByNoteId(noteId: Long): List<NoteTagCrossRef>
}

