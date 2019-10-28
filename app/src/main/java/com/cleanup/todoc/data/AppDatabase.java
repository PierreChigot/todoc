package com.cleanup.todoc.data;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.cleanup.todoc.MainApplication;
import com.cleanup.todoc.model.Project;
import com.cleanup.todoc.model.Task;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Database(entities = {Project.class, Task.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract ProjectDao projectDao();

    public abstract TaskDao taskDao();

    private static AppDatabase sInstance;

    public synchronized static AppDatabase getInstance() {
        if (sInstance == null) {
            sInstance = buildDatabase();
        }
        return sInstance;
    }

    public static AppDatabase buildDatabase() {
        return Room.databaseBuilder(MainApplication.getInstance(), AppDatabase.class,"Database.db")
                .addCallback(new Callback() {
                    @Override
                    public void onCreate(@NonNull SupportSQLiteDatabase db) {
                        super.onCreate(db);

                        // TODO PIERRE Pas forcément besoin de cette méthode,
                        //  tu as accès à ta base de donnée ici avec la variable passée en paramètre "db"
                        Executors.newSingleThreadScheduledExecutor().execute(new Runnable() {
                            @Override
                            public void run() {
                                getInstance().projectDao().insertAll(Project.getAllProjects());
                                //getInstance(MainApplication.getInstance()).projectDao().insertAll(Project.getAllProjects());
                            }
                        });
                    }
                })
                .build();

            /*synchronized (AppDatabase.class) {
                if (sInstance == null) {
                    sInstance = Room.databaseBuilder(
                            MainApplication.getInstance(),
                            AppDatabase.class,

                    ).build();
                    //sInstance.populateInitialData();
                }
            }


        return sInstance;*/
    }

    // TODO PIERRE C'était une meilleure idée que le 'Project.getAllProjects()' ;) )
   /* private void populateInitialData() {
        if (projectDao().count() == 0) {
            runInTransaction(new Runnable() {
                @Override
                public void run() {
                    Project project1 = new Project(1L, "Projet Tartampion", 0xFFEADAD1);
                    Project project2 = new Project(2L, "Projet Lucidia", 0xFFB4CDBA);
                    Project project3 = new Project(3L, "Projet Circus", 0xFFA3CED2);

                    projectDao().createProject(project1);
                    projectDao().createProject(project2);
                    projectDao().createProject(project3);


                }
            });
        }
    }*/
}
