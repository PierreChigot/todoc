package com.cleanup.todoc.ui;

import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.cleanup.todoc.data.ProjectDao;
import com.cleanup.todoc.data.TaskDao;
import com.cleanup.todoc.model.Project;
import com.cleanup.todoc.model.Task;
import com.cleanup.todoc.utils.SingleLiveEvent;

import java.util.ArrayList;

import java.util.List;

public class MainViewModel extends ViewModel {


    @NonNull
    private ProjectDao mProjectDao;
    @NonNull
    private TaskDao mTaskDao;


    private final MediatorLiveData<List<TaskUIModel>> mUiModelsLiveData = new MediatorLiveData<>();
    private final SingleLiveEvent<ViewAction> mSingleLiveDataEvent = new SingleLiveEvent<>();


    int mSortMethod = 1;


    public MainViewModel(@NonNull ProjectDao projectDao, @NonNull TaskDao taskDao) {
        mProjectDao = projectDao;
        mTaskDao = taskDao;
        wireUpMediator();

    }

    private void wireUpMediator() {
        final LiveData<List<Project>> liveDataProject = mProjectDao.getProjectsLiveData();
        final LiveData<List<Task>> liveDataTasks = mTaskDao.getTasksLiveData();

        mUiModelsLiveData.addSource(liveDataProject, new Observer<List<Project>>() {
            @Override
            public void onChanged(List<Project> projects) {
                combineProjectsAndTasks(projects, liveDataTasks.getValue());
            }
        });
        mUiModelsLiveData.addSource(liveDataTasks, new Observer<List<Task>>() {
            @Override
            public void onChanged(List<Task> tasks) {
                combineProjectsAndTasks(liveDataProject.getValue(), tasks);
            }
        });
    }


    LiveData<List<TaskUIModel>> getUiModelsLiveData() {
        return mUiModelsLiveData;
    }

    LiveData<ViewAction> getViewActionMutableLiveData() {
        return mSingleLiveDataEvent;
    }

    void combineProjectsAndTasks(List<Project> projects, List<Task> tasks) {

        List<TaskUIModel> uiModels = new ArrayList<>();

        Log.d("Pierre", "combineProjectsAndTasks() called with: projects = [" + projects + "], tasks = [" + tasks + "]");
        if (projects == null || projects.isEmpty()) {
            initializeProjects();


            return;
        }
        if (tasks == null || tasks.isEmpty()) {
            mSingleLiveDataEvent.setValue(ViewAction.NO_TASK);
            return;

        }

        for (Project project : projects) {
            for (Task task : tasks) {
                if (project.getId() == task.getProjectId()) {
                    uiModels.add(map(project.getId(), task.getName(), project.getColor()));
                }
                /*int colorRes = -1;

                if (updatedTask.getProjectId() == 1L) {
                    colorRes = 0xFFEADAD1;
                } else if (updatedTask.getProjectId() == 2L) {
                    colorRes = 0xFFB4CDBA;
                } else if (updatedTask.getProjectId() == 3L) {
                    colorRes = 0xFFA3CED2;
                }*/
            }
        }

        mUiModelsLiveData.setValue(uiModels);
        mSingleLiveDataEvent.setValue(ViewAction.SHOW_TASKS);
    }

    private void initializeProjects() {
        Project project1 = new Project(1L, "Projet Tartampion", 0xFFEADAD1);
        Project project2 = new Project(2L, "Projet Lucidia", 0xFFB4CDBA);
        Project project3 = new Project(3L, "Projet Circus", 0xFFA3CED2);
        addProject(project1);
        addProject(project2);
        addProject(project3);
    }

    private void essais() {


        /*updatedTasks = tasksLiveData.getValue();
            if (mSortMethod == 1) {
                Collections.sort(updatedTasks, new Task.TaskAZComparator());
            } else if (mSortMethod == 2) {
                Collections.sort(updatedTasks, new Task.TaskZAComparator());
            } else if (mSortMethod == 3) {
                Collections.sort(updatedTasks, new Task.TaskOldComparator());
            } else if (mSortMethod == 4) {
                Collections.sort(updatedTasks, new Task.TaskRecentComparator());
            }
            for (Task updatedTask : updatedTasks) {

                uiModels.add(new TaskUIModel(updatedTask.getId(), updatedTask.getName(), colorRes));
            }
        */

    }

    private TaskUIModel map(long id, String name, int color) {

        return new TaskUIModel(id, name, color);
    }

    void addTask(Task task) {

        new InsertDataAsyncTask(mProjectDao, mTaskDao, task, null).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }
    void addProject(Project project) {

        new InsertDataAsyncTask(mProjectDao, mTaskDao, null, project).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }


    void deleteTask(long taskId) {
        new DeleteDataAsyncTask(mProjectDao, mTaskDao,taskId).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }


    private static class InsertDataAsyncTask extends AsyncTask<Void, Void, Void> {

        @NonNull
        private final ProjectDao mProjectDao;
        @NonNull
        private final TaskDao mTaskDao;

        private final Task mTask;

        private final Project mProject;

        private InsertDataAsyncTask(@NonNull ProjectDao projectDao, @NonNull TaskDao taskDao, Task task, Project project) {
            mProjectDao = projectDao;
            mTaskDao = taskDao;
            mTask = task;
            mProject = project;
        }


        @Override
        protected Void doInBackground(Void... voids) {
            if (mProject != null){
                mProjectDao.createProject(mProject);
            }
            if (mTask != null){
                mTaskDao.insertTask(mTask);
            }

            return null;
        }
    }
    private static class DeleteDataAsyncTask extends AsyncTask<Void, Void, Void> {

        @NonNull
        private final ProjectDao mProjectDao;
        @NonNull
        private final TaskDao mTaskDao;

        private long mTaskId;


        private DeleteDataAsyncTask(@NonNull ProjectDao projectDao, @NonNull TaskDao taskDao, long taskId) {
            mProjectDao = projectDao;
            mTaskDao = taskDao;
            mTaskId = taskId;

        }


        @Override
        protected Void doInBackground(Void... voids) {
            mTaskDao.deleteTask(01);

            return null;
        }
    }
}
