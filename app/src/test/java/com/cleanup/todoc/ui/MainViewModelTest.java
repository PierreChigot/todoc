package com.cleanup.todoc.ui;
import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;
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
import org.mockito.Mockito;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;


public class MainViewModelTest {


    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();
    private MainViewModel viewModel;

    private MutableLiveData<List<Project>> mProjectLiveData;
    private MutableLiveData<List<Task>> mTaskLiveData;
    @Before
    public void setup() {
        ProjectDao projectDao = Mockito.mock(ProjectDao.class);
        TaskDao taskDao = Mockito.mock(TaskDao.class);

        mProjectLiveData = new MutableLiveData<>();
        mTaskLiveData = new MutableLiveData<>();

        Mockito.doReturn(mProjectLiveData).when(projectDao).getProjectsLiveData();
        Mockito.doReturn(mTaskLiveData).when(taskDao).getTasksLiveData();

        viewModel = new MainViewModel(projectDao, taskDao);

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
       // viewModel.addTask(, TASK_1_PROJECT_1, );
        assertEquals(1,viewModel.getUiModelsLiveData().getValue().get(0).getId());
    }

    @Test
    public void getProjets() {
    }

    @Test
    public void deleteTask() {
    }
}