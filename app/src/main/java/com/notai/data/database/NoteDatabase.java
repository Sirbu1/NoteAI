package com.notai.data.database;

import android.content.Context;
import android.util.Log;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.notai.data.dao.NoteDao;
import com.notai.data.dao.TagDao;
import com.notai.data.model.Note;
import com.notai.data.model.NoteTagCrossRef;
import com.notai.data.model.Tag;

/**
 * Room 数据库
 */
@Database(
        entities = {Note.class, Tag.class, NoteTagCrossRef.class},
        version = 1,
        exportSchema = true
)
public abstract class NoteDatabase extends RoomDatabase {
    private static final String TAG = "NoteDatabase";
    
    public abstract NoteDao noteDao();
    public abstract TagDao tagDao();
    
    private static volatile NoteDatabase INSTANCE;
    private static final String DATABASE_NAME = "note_database";
    
    public static NoteDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (NoteDatabase.class) {
                if (INSTANCE == null) {
                    try {
                        INSTANCE = Room.databaseBuilder(
                                context.getApplicationContext(),
                                NoteDatabase.class,
                                DATABASE_NAME
                        )
                        .fallbackToDestructiveMigration() // 如果数据库版本升级失败，删除旧数据库
                        .build();
                        Log.d(TAG, "Database initialized successfully");
                    } catch (Exception e) {
                        Log.e(TAG, "Error initializing database", e);
                        throw e;
                    }
                }
            }
        }
        return INSTANCE;
    }
}

