package com.notai.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 标签实体类
 */
@Entity(tableName = "tags")
data class Tag(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val color: String = "#6200EE" // 默认紫色
) {
    companion object {
        val DEFAULT_COLORS = listOf(
            "#6200EE", // 紫色
            "#03DAC5", // 青色
            "#FF6B6B", // 红色
            "#4ECDC4", // 蓝绿色
            "#FFE66D", // 黄色
            "#A8E6CF"  // 绿色
        )
    }
}

