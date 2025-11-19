package com.notai.data.model

import androidx.room.Entity

/**
 * 笔记和标签的关联表（多对多关系）
 */
@Entity(
    tableName = "note_tag_cross_ref",
    primaryKeys = ["noteId", "tagId"]
)
data class NoteTagCrossRef(
    val noteId: Long,
    val tagId: Long
)

