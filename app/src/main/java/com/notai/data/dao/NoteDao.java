package com.notai.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.*;

import com.notai.data.model.Note;
import com.notai.data.model.NoteWithTags;

import java.util.List;

/**
 * 笔记数据访问对象
 */
@Dao
public interface NoteDao {
    
    @Query("SELECT * FROM notes ORDER BY updatedAt DESC")
    LiveData<List<Note>> getAllNotes();
    
    @Transaction
    @Query("SELECT * FROM notes ORDER BY updatedAt DESC")
    LiveData<List<NoteWithTags>> getAllNotesWithTags();
    
    @Query("SELECT * FROM notes WHERE id = :noteId")
    Note getNoteById(long noteId);
    
    @Transaction
    @Query("SELECT * FROM notes WHERE id = :noteId")
    NoteWithTags getNoteWithTagsById(long noteId);
    
    @Query("SELECT DISTINCT notes.* FROM notes " +
           "INNER JOIN note_tag_cross_ref ON notes.id = note_tag_cross_ref.noteId " +
           "INNER JOIN tags ON note_tag_cross_ref.tagId = tags.id " +
           "WHERE tags.name LIKE '%' || :query || '%' " +
           "ORDER BY notes.updatedAt DESC")
    LiveData<List<Note>> searchNotesByTag(String query);
    
    @Query("SELECT * FROM notes " +
           "WHERE title LIKE '%' || :query || '%' " +
           "OR content LIKE '%' || :query || '%' " +
           "ORDER BY updatedAt DESC")
    LiveData<List<Note>> searchNotes(String query);
    
    @Transaction
    @Query("SELECT DISTINCT notes.* FROM notes " +
           "LEFT JOIN note_tag_cross_ref ON notes.id = note_tag_cross_ref.noteId " +
           "LEFT JOIN tags ON note_tag_cross_ref.tagId = tags.id " +
           "WHERE notes.title LIKE '%' || :query || '%' " +
           "OR notes.content LIKE '%' || :query || '%' " +
           "OR tags.name LIKE '%' || :query || '%' " +
           "ORDER BY notes.updatedAt DESC")
    LiveData<List<NoteWithTags>> searchNotesWithTags(String query);
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertNote(Note note);
    
    @Update
    void updateNote(Note note);
    
    @Delete
    void deleteNote(Note note);
    
    @Query("DELETE FROM notes WHERE id = :noteId")
    void deleteNoteById(long noteId);
}

