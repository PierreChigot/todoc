package com.cleanup.todoc.data;

import android.content.ContentValues;


import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.OnConflictStrategy;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.cleanup.todoc.MainApplication;
import com.cleanup.todoc.model.Project;
import com.cleanup.todoc.model.Task;


@Database(entities = {Project.class, Task.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract ProjectDao projectDao();

    public abstract TaskDao taskDao();

    private static AppDatabase sInstance;

    public static AppDatabase getInstance() {
        if (sInstance == null) {
            synchronized (AppDatabase.class) {
                if (sInstance == null) {
                    sInstance = Room.databaseBuilder(
                            MainApplication.getInstance(),
                            AppDatabase.class,
                            "Database.db")
                    .addCallback(populateInitialData())
                    .build();

                }
            }
        }

        return sInstance;
    }
    private static Callback populateInitialData() {
        return new Callback() {
            @Override
            public void onCreate(@NonNull SupportSQLiteDatabase db) {
                super.onCreate(db);

                ContentValues project1 = new ContentValues();
                project1.put("id", 1L);
                project1.put("name", "Projet Tartampion");
                project1.put("color", 0xFFEADAD1);

                db.insert("Project", OnConflictStrategy.IGNORE, project1);

                ContentValues project2 = new ContentValues();
                project2.put("id", 2L);
                project2.put("name", "Projet Lucidia");
                project2.put("color", 0xFFB4CDBA);

                db.insert("Project", OnConflictStrategy.IGNORE, project2);

                ContentValues project3 = new ContentValues();
                project3.put("id", 3L);
                project3.put("name", "Projet Circus");
                project3.put("color", 0xFFA3CED2);

                db.insert("Project", OnConflictStrategy.IGNORE, project3);


            }
        };

    }
}