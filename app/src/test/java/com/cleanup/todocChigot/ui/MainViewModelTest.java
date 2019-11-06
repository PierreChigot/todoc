package com.cleanup.todocChigot.ui;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;


import com.cleanup.todocChigot.LiveDataTestUtil;
import com.cleanup.todocChigot.data.ProjectDao;
import com.cleanup.todocChigot.data.TaskDao;
import com.cleanup.todocChigot.model.Project;
import com.cleanup.todocChigot.model.Task;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;


public class MainViewModelTest {


    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    private MainViewModel mViewModel;

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

        mViewModel = new MainViewModel(projectDao, taskDao);

    }


         /*Project project1 = new Project(1L, "Projet Tartampion", 0xFFEADAD1);
                Project project2 = new Project(2L, "Projet Lucidia", 0xFFB4CDBA);
                Project project3 = new Project(3L, "Projet Circus", 0xFFA3CED2);*/

    @Test
    public void shouldMapCorrectlyOneTaskWithProject() throws InterruptedException {
        //Given
        List<Project> projects = new ArrayList<>();
        Project project = new Project(1L, "Project One", 0xFFEADAD1);
        projects.add(project);
        mProjectLiveData.setValue(projects);

        List<Task> tasks = new ArrayList<>();
        Task task = new Task(project.getId(),"Task One in Project One", new Date().getTime());
        tasks.add(task);
        mTaskLiveData.setValue(tasks);

        //When
        List<TaskUIModel> result = LiveDataTestUtil.getOrAwaitValue(mViewModel.getUiModelsLiveData());

        //Then
        assertEquals(1, result.size());
        assertEquals("Task One in Project One", result.get(0).getName());
        assertEquals(0xFFEADAD1, result.get(0).getColorInt());

    }
    @Test
    public void shouldMapCorrectlyTwoTasksWithOneProject() throws InterruptedException {
        //Given
        List<Project> projects = new ArrayList<>();
        Project project = new Project(1L, "Project One", 0xFFEADAD1);
        projects.add(project);
        mProjectLiveData.setValue(projects);

        List<Task> tasks = new ArrayList<>();
        Task taskOne = new Task(project.getId(),"Task One in Project One", new Date().getTime());
        tasks.add(taskOne);
        Task taskTwo = new Task(project.getId(),"Task Two in Project One", new Date().getTime());
        tasks.add(taskTwo);
        mTaskLiveData.setValue(tasks);

        //When
        List<TaskUIModel> result = LiveDataTestUtil.getOrAwaitValue(mViewModel.getUiModelsLiveData());

        //Then
        assertEquals(2, result.size());
        assertEquals("Task One in Project One", result.get(0).getName());
        assertEquals("Task Two in Project One", result.get(1).getName());
        assertEquals(0xFFEADAD1, result.get(0).getColorInt());
        assertEquals(0xFFEADAD1, result.get(1).getColorInt());

    }

    @Test
    public void shouldMapCorrectlyOneTasksWithTwoProjectsEach() throws InterruptedException {
        //Given
        List<Project> projects = new ArrayList<>();
        Project projectOne = new Project(1L, "Project One", 0xFFEADAD1);
        projects.add(projectOne);
        Project projectTwo = new Project(2L, "Project Two", 0xFFB4CDBA);
        projects.add(projectTwo);
        mProjectLiveData.setValue(projects);

        List<Task> tasks = new ArrayList<>();
        Task taskOne = new Task(projectOne.getId(),"Task One in Project One", new Date().getTime());
        tasks.add(taskOne);

        Task taskTwo = new Task(projectTwo.getId(),"Task One in Project Two", new Date().getTime());
        tasks.add(taskTwo);
        mTaskLiveData.setValue(tasks);

        //When
        List<TaskUIModel> result = LiveDataTestUtil.getOrAwaitValue(mViewModel.getUiModelsLiveData());

        //Then
        assertEquals(2, result.size());
        assertEquals("Task One in Project One", result.get(0).getName());
        assertEquals("Task One in Project Two", result.get(1).getName());
        assertEquals(0xFFEADAD1, result.get(0).getColorInt());
        assertEquals(0xFFB4CDBA, result.get(1).getColorInt());

    }
    @Test
    public void shouldMapCorrectlyTwoTasksWithTwoProjectsEach() throws InterruptedException {
        //Given
        List<Project> projects = new ArrayList<>();
        Project projectOne = new Project(1L, "Project One", 0xFFEADAD1);
        projects.add(projectOne);
        Project projectTwo = new Project(2L, "Project Two", 0xFFB4CDBA);
        projects.add(projectTwo);
        mProjectLiveData.setValue(projects);

        List<Task> tasks = new ArrayList<>();
        Task taskOneInProjectOne = new Task(projectOne.getId(),"Task One in Project One", new Date().getTime());
        tasks.add(taskOneInProjectOne);
        Task taskTwoInProjectOne = new Task(projectOne.getId(),"Task Two in Project One", new Date().getTime());
        tasks.add(taskTwoInProjectOne);

        Task taskOneInProjectTwo = new Task(projectTwo.getId(),"Task One in Project Two", new Date().getTime());
        tasks.add(taskOneInProjectTwo);
        Task taskTwoInProjectTwo = new Task(projectTwo.getId(),"Task Two in Project Two", new Date().getTime());
        tasks.add(taskTwoInProjectTwo);
        mTaskLiveData.setValue(tasks);

        //When
        List<TaskUIModel> result = LiveDataTestUtil.getOrAwaitValue(mViewModel.getUiModelsLiveData());

        //Then
        assertEquals(4, result.size());
        assertEquals("Task One in Project One", result.get(0).getName());
        assertEquals("Task Two in Project One", result.get(1).getName());
        assertEquals("Task One in Project Two", result.get(2).getName());
        assertEquals("Task Two in Project Two", result.get(3).getName());
        assertEquals(0xFFEADAD1, result.get(0).getColorInt());
        assertEquals(0xFFEADAD1, result.get(1).getColorInt());
        assertEquals(0xFFB4CDBA, result.get(2).getColorInt());
        assertEquals(0xFFB4CDBA, result.get(3).getColorInt());

    }
    @Test
    public void shouldSortAlphabeticalCorrectlyTwoTasksWithTwoProjectsEach() throws InterruptedException {
        //Given
        List<Project> projects = new ArrayList<>();
        Project projectOne = new Project(1L, "Project One", 0xFFEADAD1);
        projects.add(projectOne);
        Project projectTwo = new Project(2L, "Project Two", 0xFFB4CDBA);
        projects.add(projectTwo);
        mProjectLiveData.setValue(projects);

        List<Task> tasks = new ArrayList<>();
        Task taskOneInProjectOne = new Task(projectOne.getId(),"aa", new Date().getTime());
        tasks.add(taskOneInProjectOne);
        Task taskTwoInProjectOne = new Task(projectOne.getId(),"zz", new Date().getTime());
        tasks.add(taskTwoInProjectOne);

        Task taskOneInProjectTwo = new Task(projectTwo.getId(),"gg", new Date().getTime());
        tasks.add(taskOneInProjectTwo);
        Task taskTwoInProjectTwo = new Task(projectTwo.getId(),"bb", new Date().getTime());
        tasks.add(taskTwoInProjectTwo);
        mTaskLiveData.setValue(tasks);

        mViewModel.sortingTasks(SortingMethod.ALPHABETICAL);

        //When
        List<TaskUIModel> result = LiveDataTestUtil.getOrAwaitValue(mViewModel.getUiModelsLiveData());

        //Then
        assertEquals(4, result.size());
        assertEquals("aa", result.get(0).getName());
        assertEquals("bb", result.get(1).getName());
        assertEquals("gg", result.get(2).getName());
        assertEquals("zz", result.get(3).getName());
        assertEquals(0xFFEADAD1, result.get(0).getColorInt());
        assertEquals(0xFFB4CDBA, result.get(1).getColorInt());
        assertEquals(0xFFB4CDBA, result.get(2).getColorInt());
        assertEquals(0xFFEADAD1, result.get(3).getColorInt());

    }
    @Test
    public void shouldSortAlphabeticalInvertedCorrectlyTwoTasksWithTwoProjectsEach() throws InterruptedException {
        //Given
        List<Project> projects = new ArrayList<>();
        Project projectOne = new Project(1L, "Project One", 0xFFEADAD1);
        projects.add(projectOne);
        Project projectTwo = new Project(2L, "Project Two", 0xFFB4CDBA);
        projects.add(projectTwo);
        mProjectLiveData.setValue(projects);

        List<Task> tasks = new ArrayList<>();
        Task taskOneInProjectOne = new Task(projectOne.getId(),"aa", new Date().getTime());
        tasks.add(taskOneInProjectOne);
        Task taskTwoInProjectOne = new Task(projectOne.getId(),"zz", new Date().getTime());
        tasks.add(taskTwoInProjectOne);

        Task taskOneInProjectTwo = new Task(projectTwo.getId(),"gg", new Date().getTime());
        tasks.add(taskOneInProjectTwo);
        Task taskTwoInProjectTwo = new Task(projectTwo.getId(),"bb", new Date().getTime());
        tasks.add(taskTwoInProjectTwo);
        mTaskLiveData.setValue(tasks);

        mViewModel.sortingTasks(SortingMethod.ALPHABETICAL_INVERTED);

        //When
        List<TaskUIModel> result = LiveDataTestUtil.getOrAwaitValue(mViewModel.getUiModelsLiveData());

        //Then
        assertEquals(4, result.size());
        assertEquals("aa", result.get(3).getName());
        assertEquals("bb", result.get(2).getName());
        assertEquals("gg", result.get(1).getName());
        assertEquals("zz", result.get(0).getName());
        assertEquals(0xFFEADAD1, result.get(3).getColorInt());
        assertEquals(0xFFB4CDBA, result.get(2).getColorInt());
        assertEquals(0xFFB4CDBA, result.get(1).getColorInt());
        assertEquals(0xFFEADAD1, result.get(0).getColorInt());

    }
    @Test
    public void shouldSortOldFirstCorrectlyTwoTasksWithTwoProjectsEach() throws InterruptedException {
        //Given
        List<Project> projects = new ArrayList<>();
        Project projectOne = new Project(1L, "Project One", 0xFFEADAD1);
        projects.add(projectOne);
        Project projectTwo = new Project(2L, "Project Two", 0xFFB4CDBA);
        projects.add(projectTwo);
        mProjectLiveData.setValue(projects);

        List<Task> tasks = new ArrayList<>();
        Task taskOneInProjectOne = new Task(projectOne.getId(),"aa", 1572623000);
        tasks.add(taskOneInProjectOne);
        Task taskTwoInProjectOne = new Task(projectOne.getId(),"zz", 1572623001);
        tasks.add(taskTwoInProjectOne);

        Task taskOneInProjectTwo = new Task(projectTwo.getId(),"gg", 1572623002);
        tasks.add(taskOneInProjectTwo);
        Task taskTwoInProjectTwo = new Task(projectTwo.getId(),"bb", 1572623003);
        tasks.add(taskTwoInProjectTwo);
        mTaskLiveData.setValue(tasks);

        mViewModel.sortingTasks(SortingMethod.OLD_FIRST);

        //When
        List<TaskUIModel> result = LiveDataTestUtil.getOrAwaitValue(mViewModel.getUiModelsLiveData());

        //Then
        assertEquals(4, result.size());
        assertEquals("aa", result.get(0).getName());
        assertEquals("zz", result.get(1).getName());
        assertEquals("gg", result.get(2).getName());
        assertEquals("bb", result.get(3).getName());
        assertEquals(0xFFEADAD1, result.get(0).getColorInt());
        assertEquals(0xFFEADAD1, result.get(1).getColorInt());
        assertEquals(0xFFB4CDBA, result.get(2).getColorInt());
        assertEquals(0xFFB4CDBA, result.get(3).getColorInt());

    }
    @Test
    public void shouldSortRecentFirstCorrectlyTwoTasksWithTwoProjectsEach() throws InterruptedException {
        //Given
        List<Project> projects = new ArrayList<>();
        Project projectOne = new Project(1L, "Project One", 0xFFEADAD1);
        projects.add(projectOne);
        Project projectTwo = new Project(2L, "Project Two", 0xFFB4CDBA);
        projects.add(projectTwo);
        mProjectLiveData.setValue(projects);

        List<Task> tasks = new ArrayList<>();
        Task taskOneInProjectOne = new Task(projectOne.getId(),"aa", 1572623000);
        tasks.add(taskOneInProjectOne);
        Task taskTwoInProjectOne = new Task(projectOne.getId(),"zz", 1572623001);
        tasks.add(taskTwoInProjectOne);

        Task taskOneInProjectTwo = new Task(projectTwo.getId(),"gg", 1572623002);
        tasks.add(taskOneInProjectTwo);
        Task taskTwoInProjectTwo = new Task(projectTwo.getId(),"bb", 1572623003);
        tasks.add(taskTwoInProjectTwo);
        mTaskLiveData.setValue(tasks);

        mViewModel.sortingTasks(SortingMethod.RECENT_FIRST);

        //When
        List<TaskUIModel> result = LiveDataTestUtil.getOrAwaitValue(mViewModel.getUiModelsLiveData());

        //Then
        assertEquals(4, result.size());
        assertEquals("aa", result.get(3).getName());
        assertEquals("zz", result.get(2).getName());
        assertEquals("gg", result.get(1).getName());
        assertEquals("bb", result.get(0).getName());
        assertEquals(0xFFEADAD1, result.get(3).getColorInt());
        assertEquals(0xFFEADAD1, result.get(2).getColorInt());
        assertEquals(0xFFB4CDBA, result.get(1).getColorInt());
        assertEquals(0xFFB4CDBA, result.get(0).getColorInt());

    }
    @Test
    public void shouldSortByProjectCorrectlyTwoTasksWithTwoProjectsEach() throws InterruptedException {
        //Given
        List<Project> projects = new ArrayList<>();
        Project projectOne = new Project(1L, "Project One", 0xFFEADAD1);
        projects.add(projectOne);
        Project projectTwo = new Project(2L, "Project Two", 0xFFB4CDBA);
        projects.add(projectTwo);
        mProjectLiveData.setValue(projects);

        List<Task> tasks = new ArrayList<>();
        Task taskOneInProjectOne = new Task(projectOne.getId(),"aa", 1572623000);
        tasks.add(taskOneInProjectOne);


        Task taskOneInProjectTwo = new Task(projectTwo.getId(),"gg", 1572623002);
        tasks.add(taskOneInProjectTwo);

        Task taskTwoInProjectOne = new Task(projectOne.getId(),"zz", 1572623001);
        tasks.add(taskTwoInProjectOne);

        Task taskTwoInProjectTwo = new Task(projectTwo.getId(),"bb", 1572623003);
        tasks.add(taskTwoInProjectTwo);
        mTaskLiveData.setValue(tasks);

        mViewModel.sortingTasks(SortingMethod.PROJECT);

        //When
        List<TaskUIModel> result = LiveDataTestUtil.getOrAwaitValue(mViewModel.getUiModelsLiveData());

        //Then
        assertEquals(4, result.size());
        assertEquals("aa", result.get(0).getName());
        assertEquals("zz", result.get(1).getName());
        assertEquals("gg", result.get(2).getName());
        assertEquals("bb", result.get(3).getName());
        assertEquals(0xFFEADAD1, result.get(0).getColorInt());
        assertEquals(0xFFEADAD1, result.get(1).getColorInt());
        assertEquals(0xFFB4CDBA, result.get(2).getColorInt());
        assertEquals(0xFFB4CDBA, result.get(3).getColorInt());

    }
    //TODO est-ce que ça vaut le coup :
    @Test
    public void shouldDeleteTaskCorrectly() throws InterruptedException {
        //Given
        List<Project> projects = new ArrayList<>();
        Project projectOne = new Project(1L, "Project One", 0xFFEADAD1);
        projects.add(projectOne);
        Project projectTwo = new Project(2L, "Project Two", 0xFFB4CDBA);
        projects.add(projectTwo);
        mProjectLiveData.setValue(projects);

        List<Task> tasks = new ArrayList<>();
        Task taskOneInProjectOne = new Task(projectOne.getId(),"aa", 1572623000);
        tasks.add(taskOneInProjectOne);


        Task taskOneInProjectTwo = new Task(projectTwo.getId(),"gg", 1572623002);
        tasks.add(taskOneInProjectTwo);

        Task taskTwoInProjectOne = new Task(projectOne.getId(),"zz", 1572623001);
        tasks.add(taskTwoInProjectOne);

        Task taskTwoInProjectTwo = new Task(projectTwo.getId(),"bb", 1572623003);
        tasks.add(taskTwoInProjectTwo);
        mTaskLiveData.setValue(tasks);

        tasks.remove(taskOneInProjectTwo);

        //When
        List<TaskUIModel> result = LiveDataTestUtil.getOrAwaitValue(mViewModel.getUiModelsLiveData());

        //Then
        assertEquals(3, result.size());
        assertEquals("aa", result.get(0).getName());
        assertEquals("zz", result.get(1).getName());
        assertEquals("bb", result.get(2).getName());
        assertEquals(0xFFEADAD1, result.get(0).getColorInt());
        assertEquals(0xFFEADAD1, result.get(1).getColorInt());
        assertEquals(0xFFB4CDBA, result.get(2).getColorInt());

    }
}