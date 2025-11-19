package com.notai;

import android.app.Application;

import com.notai.data.database.NoteDatabase;
import com.notai.data.repository.NoteRepository;
import com.notai.data.repository.TagRepository;

/**
 * Application 类，用于初始化数据库和仓库
 */
public class NoteAIApplication extends Application {
    
    private NoteDatabase database;
    private NoteRepository noteRepository;
    private TagRepository tagRepository;
    
    private static volatile NoteAIApplication INSTANCE;
    
    public static NoteAIApplication getInstance() {
        if (INSTANCE == null) {
            throw new IllegalStateException("Application not initialized");
        }
        return INSTANCE;
    }
    
    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = this;
    }
    
    public NoteDatabase getDatabase() {
        if (database == null) {
            database = NoteDatabase.getDatabase(this);
        }
        return database;
    }
    
    public NoteRepository getNoteRepository() {
        if (noteRepository == null) {
            noteRepository = new NoteRepository(
                    getDatabase().noteDao(),
                    getDatabase().tagDao()
            );
        }
        return noteRepository;
    }
    
    public TagRepository getTagRepository() {
        if (tagRepository == null) {
            tagRepository = new TagRepository(getDatabase().tagDao());
        }
        return tagRepository;
    }
}

