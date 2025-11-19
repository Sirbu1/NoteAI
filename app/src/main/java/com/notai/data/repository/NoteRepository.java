package com.notai.data.repository;

import androidx.lifecycle.LiveData;

import com.notai.data.dao.NoteDao;
import com.notai.data.dao.TagDao;
import com.notai.data.model.Note;
import com.notai.data.model.NoteTagCrossRef;
import com.notai.data.model.NoteWithTags;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * 笔记仓库类（数据层）
 */
public class NoteRepository {
    private final NoteDao noteDao;
    private final TagDao tagDao;
    private final Executor executor;

    public NoteRepository(NoteDao noteDao, TagDao tagDao) {
        this.noteDao = noteDao;
        this.tagDao = tagDao;
        this.executor = Executors.newSingleThreadExecutor();
    }

    public LiveData<List<Note>> getAllNotes() {
        return noteDao.getAllNotes();
    }

    public LiveData<List<NoteWithTags>> getAllNotesWithTags() {
        return noteDao.getAllNotesWithTags();
    }

    public Note getNoteById(long noteId) {
        return noteDao.getNoteById(noteId);
    }

    public NoteWithTags getNoteWithTagsById(long noteId) {
        return noteDao.getNoteWithTagsById(noteId);
    }

    public LiveData<List<Note>> searchNotes(String query) {
        return noteDao.searchNotes(query);
    }

    public LiveData<List<NoteWithTags>> searchNotesWithTags(String query) {
        return noteDao.searchNotesWithTags(query);
    }

    public void insertNote(Note note, List<Long> tagIds, InsertCallback callback) {
        executor.execute(() -> {
            long noteId = noteDao.insertNote(note);
            // 关联标签
            if (tagIds != null) {
                for (Long tagId : tagIds) {
                    tagDao.insertNoteTagCrossRef(new NoteTagCrossRef(noteId, tagId));
                }
            }
            if (callback != null) {
                callback.onInserted(noteId);
            }
        });
    }

    public void updateNote(Note note, List<Long> tagIds) {
        executor.execute(() -> {
            noteDao.updateNote(note);
            // 删除旧的关联
            tagDao.deleteNoteTagRefsByNoteId(note.getId());
            // 添加新的关联
            if (tagIds != null) {
                for (Long tagId : tagIds) {
                    tagDao.insertNoteTagCrossRef(new NoteTagCrossRef(note.getId(), tagId));
                }
            }
        });
    }

    public void deleteNote(long noteId) {
        executor.execute(() -> {
            // 先删除关联关系
            tagDao.deleteNoteTagRefsByNoteId(noteId);
            // 再删除笔记
            noteDao.deleteNoteById(noteId);
        });
    }

    public interface InsertCallback {
        void onInserted(long noteId);
    }
}

