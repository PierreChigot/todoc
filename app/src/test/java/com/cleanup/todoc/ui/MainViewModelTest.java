package com.cleanup.todoc.ui;
import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.test.core.app.ApplicationProvider;


import androidx.room.Room;


import com.cleanup.todoc.data.AppDatabase;
import com.cleanup.todoc.data.ProjectDao;
import com.cleanup.todoc.data.TaskDao;
import com.cleanup.todoc.model.Project;
import com.cleanup.todoc.model.Task;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Date;
import static org.junit.Assert.*;


public class MainViewModelTest {


    private AppDatabase mDatabase;
    private TaskDao mTaskDao;
    private ProjectDao mProjectDao;

    private static long TASK_ID = 1;
    private static Project PROJECT_1 = new Project(4L, "PROJECT 1", 0xFFEADAD1);
    private static Task TASK_1_PROJECT_1 = new Task(4L, "task 1", new Date().getTime());

    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void createDb() {

        Context context = ApplicationProvider.getApplicationContext();
        this.mDatabase = Room.inMemoryDatabaseBuilder(context,
                AppDatabase.class).allowMainThreadQueries().build();
        mTaskDao = mDatabase.taskDao();
        mProjectDao = mDatabase.projectDao();
        mTaskDao.deleteAll();
    }

    @After
    public void closeDb() {
        mDatabase.close();
    }


    @Test
    public void sortingTasks() {
    }

    @Test
    public void addTask() {
        MainViewModel viewModel = new MainViewModel(mProjectDao,mTaskDao);
        final Task task1 = new Task(1, "task 1", new Date().getTime());
        final Task task2 = new Task(2, "task 2", new Date().getTime());
        final Task task3 = new Task(3, "task 3", new Date().getTime());
        final Task task4 = new Task(4, "task 4", new Date().getTime());
        viewModel.addTask(, TASK_1_PROJECT_1, );
        assertEquals(1,viewModel.getUiModelsLiveData().getValue().get(0).getId());
    }
//TODO mock db ou test instru pour view model
    @Test
    public void getProjets() {
    }

    @Test
    public void deleteTask() {
    }
}