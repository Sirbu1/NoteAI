package com.notai.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

/**
 * 笔记实体类
 */
@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val content: String,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    fun getFormattedCreatedAt(): String {
        return Date(createdAt).toString()
    }
    
    fun getFormattedUpdatedAt(): String {
        return Date(updatedAt).toString()
    }
}

