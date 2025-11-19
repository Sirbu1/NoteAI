package com.notai.data.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

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
@TypeConverters({Converters.class})
public abstract class NoteDatabase extends RoomDatabase {
    public abstract NoteDao noteDao();
    public abstract TagDao tagDao();
    
    private static volatile NoteDatabase INSTANCE;
    private static final String DATABASE_NAME = "note_database";
    
    public static NoteDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (NoteDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            NoteDatabase.class,
                            DATABASE_NAME
                    ).build();
                }
            }
        }
        return INSTANCE;
    }
}

