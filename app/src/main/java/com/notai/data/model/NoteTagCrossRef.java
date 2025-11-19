package com.notai.data.model;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.Ignore;

import java.util.Objects;

/**
 * 笔记和标签的关联表（多对多关系）
 */
@Entity(
        tableName = "note_tag_cross_ref",
        primaryKeys = {"noteId", "tagId"},
        indices = {
                @Index(value = {"noteId"}),
                @Index(value = {"tagId"})
        }
)
public class NoteTagCrossRef {
    private long noteId;
    private long tagId;

    public NoteTagCrossRef() {
    }

    @Ignore
    public NoteTagCrossRef(long noteId, long tagId) {
        this.noteId = noteId;
        this.tagId = tagId;
    }

    public long getNoteId() {
        return noteId;
    }

    public void setNoteId(long noteId) {
        this.noteId = noteId;
    }

    public long getTagId() {
        return tagId;
    }

    public void setTagId(long tagId) {
        this.tagId = tagId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NoteTagCrossRef that = (NoteTagCrossRef) o;
        return noteId == that.noteId &&
                tagId == that.tagId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(noteId, tagId);
    }
}

