package com.notai.data.dao

import androidx.room.*
import com.notai.data.model.Note
import com.notai.data.model.NoteWithTags
import kotlinx.coroutines.flow.Flow

/**
 * 笔记数据访问对象
 */
@Dao
interface NoteDao {
    
    @Query("SELECT * FROM notes ORDER BY updatedAt DESC")
    fun getAllNotes(): Flow<List<Note>>
    
    @Transaction
    @Query("SELECT * FROM notes ORDER BY updatedAt DESC")
    fun getAllNotesWithTags(): Flow<List<NoteWithTags>>
    
    @Query("SELECT * FROM notes WHERE id = :noteId")
    suspend fun getNoteById(noteId: Long): Note?
    
    @Transaction
    @Query("SELECT * FROM notes WHERE id = :noteId")
    suspend fun getNoteWithTagsById(noteId: Long): NoteWithTags?
    
    @Query("""
        SELECT DISTINCT notes.* FROM notes
        INNER JOIN note_tag_cross_ref ON notes.id = note_tag_cross_ref.noteId
        INNER JOIN tags ON note_tag_cross_ref.tagId = tags.id
        WHERE tags.name LIKE '%' || :query || '%'
        ORDER BY notes.updatedAt DESC
    """)
    fun searchNotesByTag(query: String): Flow<List<Note>>
    
    @Query("""
        SELECT * FROM notes 
        WHERE title LIKE '%' || :query || '%' 
        OR content LIKE '%' || :query || '%'
        ORDER BY updatedAt DESC
    """)
    fun searchNotes(query: String): Flow<List<Note>>
    
    @Transaction
    @Query("""
        SELECT DISTINCT notes.* FROM notes
        LEFT JOIN note_tag_cross_ref ON notes.id = note_tag_cross_ref.noteId
        LEFT JOIN tags ON note_tag_cross_ref.tagId = tags.id
        WHERE notes.title LIKE '%' || :query || '%' 
        OR notes.content LIKE '%' || :query || '%'
        OR tags.name LIKE '%' || :query || '%'
        ORDER BY notes.updatedAt DESC
    """)
    fun searchNotesWithTags(query: String): Flow<List<NoteWithTags>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: Note): Long
    
    @Update
    suspend fun updateNote(note: Note)
    
    @Delete
    suspend fun deleteNote(note: Note)
    
    @Query("DELETE FROM notes WHERE id = :noteId")
    suspend fun deleteNoteById(noteId: Long)
}

