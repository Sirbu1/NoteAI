package com.notai.data.repository

import com.notai.data.dao.TagDao
import com.notai.data.model.Tag
import kotlinx.coroutines.flow.Flow

/**
 * 标签仓库类（数据层）
 */
class TagRepository(
    private val tagDao: TagDao
) {
    
    fun getAllTags(): Flow<List<Tag>> = tagDao.getAllTags()
    
    suspend fun getTagById(tagId: Long): Tag? = tagDao.getTagById(tagId)
    
    suspend fun getTagByName(name: String): Tag? = tagDao.getTagByName(name)
    
    suspend fun insertTag(tag: Tag): Long = tagDao.insertTag(tag)
    
    suspend fun updateTag(tag: Tag) = tagDao.updateTag(tag)
    
    suspend fun deleteTag(tagId: Long) {
        // 先删除关联关系
        tagDao.deleteNoteTagRefsByTagId(tagId)
        // 再删除标签
        tagDao.deleteTagById(tagId)
    }
}

