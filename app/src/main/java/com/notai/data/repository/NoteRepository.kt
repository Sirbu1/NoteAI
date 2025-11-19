package com.notai.data.repository

import com.notai.data.dao.NoteDao
import com.notai.data.dao.TagDao
import com.notai.data.model.Note
import com.notai.data.model.NoteTagCrossRef
import com.notai.data.model.NoteWithTags
import kotlinx.coroutines.flow.Flow

/**
 * 笔记仓库类（数据层）
 */
class NoteRepository(
    private val noteDao: NoteDao,
    private val tagDao: TagDao
) {
    
    fun getAllNotes(): Flow<List<Note>> = noteDao.getAllNotes()
    
    fun getAllNotesWithTags(): Flow<List<NoteWithTags>> = noteDao.getAllNotesWithTags()
    
    suspend fun getNoteById(noteId: Long): Note? = noteDao.getNoteById(noteId)
    
    suspend fun getNoteWithTagsById(noteId: Long): NoteWithTags? = 
        noteDao.getNoteWithTagsById(noteId)
    
    fun searchNotes(query: String): Flow<List<Note>> = noteDao.searchNotes(query)
    
    fun searchNotesWithTags(query: String): Flow<List<NoteWithTags>> = 
        noteDao.searchNotesWithTags(query)
    
    suspend fun insertNote(note: Note, tagIds: List<Long> = emptyList()): Long {
        val noteId = noteDao.insertNote(note)
        // 关联标签
        tagIds.forEach { tagId ->
            tagDao.insertNoteTagCrossRef(NoteTagCrossRef(noteId, tagId))
        }
        return noteId
    }
    
    suspend fun updateNote(note: Note, tagIds: List<Long> = emptyList()) {
        noteDao.updateNote(note)
        // 删除旧的关联
        tagDao.deleteNoteTagRefsByNoteId(note.id)
        // 添加新的关联
        tagIds.forEach { tagId ->
            tagDao.insertNoteTagCrossRef(NoteTagCrossRef(note.id, tagId))
        }
    }
    
    suspend fun deleteNote(noteId: Long) {
        // 先删除关联关系
        tagDao.deleteNoteTagRefsByNoteId(noteId)
        // 再删除笔记
        noteDao.deleteNoteById(noteId)
    }
}

