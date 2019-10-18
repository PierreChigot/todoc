package com.cleanup.todoc.data;

import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;

import com.cleanup.todoc.model.Project;
import com.cleanup.todoc.model.Task;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;


import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class DaoTest {

    private AppDatabase mDatabase;
    private TaskDao mTaskDao;
    private ProjectDao mProjectDao;

    private static long TASK_ID = 1;
    private static Project PROJECT_1 = new Project(4L, "PROJECT 1", 0xFFEADAD1);
    private static Task TASK_1_PROJECT_1 = new Task(1, 4L, "task 1", new Date().getTime());

    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        this.mDatabase = Room.inMemoryDatabaseBuilder(context,
                AppDatabase.class).allowMainThreadQueries().build();
        mTaskDao = mDatabase.taskDao();
        mProjectDao = mDatabase.projectDao();
    }

    @After
    public void closeDb() {
        mDatabase.close();
    }
    @Test
    public void insertAndGetProject() throws InterruptedException {
        mProjectDao.createProject(PROJECT_1);

        Project project = LiveDataTestUtil.getValue(mProjectDao.getProjectsLiveData()).get(0);
        assertTrue(project.getName().equals(PROJECT_1.getName()) && project.getId() == PROJECT_1.getId());
    }
    @Test
    public void insertAndGetTask() throws InterruptedException {
        mProjectDao.createProject(PROJECT_1);
        mTaskDao.insertTask(TASK_1_PROJECT_1);

        Task task = LiveDataTestUtil.getValue(mTaskDao.getTasksLiveData()).get(0);
        assertTrue(task.getName().equals(TASK_1_PROJECT_1.getName()) && task.getId() == TASK_ID);
    }
    @Test
    public void getTasksWhenNoTaskInserted() throws InterruptedException {
        List<Task> tasks = LiveDataTestUtil.getValue(mTaskDao.getTasksLiveData());
        assertTrue(tasks.isEmpty());
    }
    @Test
    public void getProjectsWhenNoProjectInserted() throws InterruptedException {
        List<Project> projects = LiveDataTestUtil.getValue(mProjectDao.getProjectsLiveData());
        assertTrue(projects.isEmpty());
    }
    @Test
    public void insertAndUpdateTask() throws InterruptedException {
        mProjectDao.createProject(PROJECT_1);
        mTaskDao.insertTask(TASK_1_PROJECT_1);

        Task taskInserted = LiveDataTestUtil.getValue(mTaskDao.getTasksLiveData()).get(0);
        taskInserted.setName("essai");
        mTaskDao.updateTask(taskInserted);

        Task task = LiveDataTestUtil.getValue(mTaskDao.getTasksLiveData()).get(0);
        assertTrue(task.getName().equals("essai") && task.getId() == TASK_ID);
    }

    @Test
    public void insetAndDeleteTask() throws InterruptedException {
        mProjectDao.createProject(PROJECT_1);
        mTaskDao.insertTask(TASK_1_PROJECT_1);

        Task task = LiveDataTestUtil.getValue(mTaskDao.getTasksLiveData()).get(0);
        assertTrue(task.getName().equals(TASK_1_PROJECT_1.getName()) && task.getId() == TASK_ID);

        mTaskDao.deleteTask(TASK_ID);
        List<Task> tasks = LiveDataTestUtil.getValue(mTaskDao.getTasksLiveData());
        assertFalse(tasks.contains(task));

    }
}