package com.notai.data.model

import androidx.room.Embedded
import androidx.room.Relation

/**
 * 笔记及其关联的标签（用于查询）
 */
data class NoteWithTags(
    @Embedded val note: Note,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = androidx.room.Junction(
            value = NoteTagCrossRef::class,
            parentColumn = "noteId",
            entityColumn = "tagId"
        )
    )
    val tags: List<Tag>
)

