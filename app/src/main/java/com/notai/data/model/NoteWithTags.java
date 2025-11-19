package com.notai.data.model;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;
import java.util.Objects;

/**
 * 笔记及其关联的标签（用于查询）
 */
public class NoteWithTags {
    @Embedded
    private Note note;

    @Relation(
            parentColumn = "id",
            entityColumn = "id",
            associateBy = @androidx.room.Junction(
                    value = NoteTagCrossRef.class,
                    parentColumn = "noteId",
                    entityColumn = "tagId"
            )
    )
    private List<Tag> tags;

    public NoteWithTags() {
    }

    public NoteWithTags(Note note, List<Tag> tags) {
        this.note = note;
        this.tags = tags;
    }

    public Note getNote() {
        return note;
    }

    public void setNote(Note note) {
        this.note = note;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NoteWithTags that = (NoteWithTags) o;
        return Objects.equals(note, that.note) &&
                Objects.equals(tags, that.tags);
    }

    @Override
    public int hashCode() {
        return Objects.hash(note, tags);
    }
}

