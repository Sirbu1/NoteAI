package com.notai

import android.app.Application
import com.notai.data.database.NoteDatabase
import com.notai.data.repository.NoteRepository
import com.notai.data.repository.TagRepository

/**
 * Application 类，用于初始化数据库和仓库
 */
class NoteAIApplication : Application() {
    
    val database by lazy { NoteDatabase.getDatabase(this) }
    val noteRepository by lazy { NoteRepository(database.noteDao(), database.tagDao()) }
    val tagRepository by lazy { TagRepository(database.tagDao()) }
    
    companion object {
        @Volatile
        private var INSTANCE: NoteAIApplication? = null
        
        fun getInstance(): NoteAIApplication {
            return INSTANCE ?: throw IllegalStateException("Application not initialized")
        }
    }
    
    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
    }
}

