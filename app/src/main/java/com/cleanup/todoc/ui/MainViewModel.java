package com.cleanup.todoc.ui;

import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.cleanup.todoc.data.ProjectDao;
import com.cleanup.todoc.data.TaskDao;
import com.cleanup.todoc.model.Project;
import com.cleanup.todoc.model.Task;
import com.cleanup.todoc.utils.SingleLiveEvent;


import java.util.ArrayList;

import java.util.Collections;
import java.util.List;

class MainViewModel extends ViewModel {


    @NonNull
    private ProjectDao mProjectDao;
    @NonNull
    private TaskDao mTaskDao;
    private long mLastId = -1;


    private final MediatorLiveData<List<TaskUIModel>> mUiModelsLiveData = new MediatorLiveData<>();
    private final SingleLiveEvent<ViewAction> mSingleLiveDataEvent = new SingleLiveEvent<>();
    private final MutableLiveData<List<Project>>mProjectLiveData = new MutableLiveData<>();
    private final MutableLiveData<Integer> mSortingMethodLiveData = new MutableLiveData<>();


    private Integer mSortingMethod = -1;

    //TODO Est-ce qu'il faut un ContentProvider comme dans le cours OC ?
    MainViewModel(@NonNull ProjectDao projectDao, @NonNull TaskDao taskDao) {
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
                combineProjectsAndTasks(projects, liveDataTasks.getValue(), mSortingMethodLiveData.getValue());
            }
        });
        mUiModelsLiveData.addSource(liveDataTasks, new Observer<List<Task>>() {
            @Override
            public void onChanged(List<Task> tasks) {
                combineProjectsAndTasks(liveDataProject.getValue(), tasks, mSortingMethodLiveData.getValue());
            }
        });
        mUiModelsLiveData.addSource(mSortingMethodLiveData, new Observer<Integer>() {
            @Override
            public void onChanged(Integer sortingMethod) {
                mSortingMethod = sortingMethod;
                combineProjectsAndTasks(liveDataProject.getValue(),liveDataTasks.getValue(), mSortingMethod);
            }
        });
    }


    LiveData<List<TaskUIModel>> getUiModelsLiveData() {
        return mUiModelsLiveData;
    }

    LiveData<ViewAction> getViewActionMutableLiveData() {
        return mSingleLiveDataEvent;
    }

    MutableLiveData<List<Project>> getProjectLiveData(){
        return mProjectLiveData;
    }

    private void combineProjectsAndTasks(List<Project> projects, List<Task> tasks, Integer sortingMethod) {

        List<TaskUIModel> uiModels = new ArrayList<>();


        Log.d("Pierre", "combineProjectsAndTasks() called with: projects = [" + projects + "], tasks = [" + tasks + "]");
        /*if (projects == null || projects.isEmpty()) {
            //initializeProjects();
            return;
        }*/
        if (tasks == null || tasks.isEmpty()) {
            mSingleLiveDataEvent.setValue(ViewAction.NO_TASK);
            return;
        }
        if (sortingMethod == null) {
            sortingMethod = 1;
        }
        if (sortingMethod == 1 ) {
            Collections.sort(tasks, new Task.TaskAZComparator());
            for (Task task : tasks) {
                if (task.getProject() != null) {
                    uiModels.add(map(task.getId(), task.getName(), task.getProject().getColor()));
                }
            }
        } else if (sortingMethod == 2) {
            Collections.sort(tasks, new Task.TaskZAComparator());
            for (Task task : tasks) {
                if (task.getProject() != null) {
                    uiModels.add(map(task.getId(), task.getName(), task.getProject().getColor()));
                }
            }
        } else if (sortingMethod == 3) {
            Collections.sort(tasks, new Task.TaskOldComparator());
            for (Task task : tasks) {
                if (task.getProject() != null) {
                    uiModels.add(map(task.getId(), task.getName(), task.getProject().getColor()));
                }
            }
        } else if (sortingMethod == 4) {
            for (Task task : tasks) {
                if (task.getProject() != null) {
                    uiModels.add(map(task.getId(), task.getName(), task.getProject().getColor()));
                }
            }
            Collections.sort(tasks, new Task.TaskRecentComparator());
        } else if (sortingMethod == 5) {
            for (Project project : projects) {
                for (Task task : tasks) {
                    if (project.getId() == task.getProjectId()) {
                        uiModels.add(map(project.getId(), task.getName(), project.getColor()));
                    }
                }
            }
        }
        for (TaskUIModel uiModel : uiModels) {
            if (uiModel.getId() <= mLastId){
                mLastId = uiModel.getId();
            }
        }
        mUiModelsLiveData.setValue(uiModels);
        mSingleLiveDataEvent.setValue(ViewAction.SHOW_TASKS);
        mProjectLiveData.setValue(projects);
    }


    /*private void initializeProjects() {
        Project project1 = new Project(1L, "Projet Tartampion", 0xFFEADAD1);
        Project project2 = new Project(2L, "Projet Lucidia", 0xFFB4CDBA);
        Project project3 = new Project(3L, "Projet Circus", 0xFFA3CED2);
        addProject(project1);
        addProject(project2);
        addProject(project3);
    }*/


    void SortingTasks(int sortingType) {
        mSortingMethod = sortingType;

        mSortingMethodLiveData.setValue(mSortingMethod);

    }

    private TaskUIModel map(long id, String name, int color) {

        return new TaskUIModel(id, name, color);
    }

    void addTask(Task task) {

        new InsertDataAsyncTask(mProjectDao, mTaskDao, task, null, mLastId).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    private void addProject(Project project) {

        new InsertDataAsyncTask(mProjectDao, mTaskDao, null, project,mLastId).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    void deleteTask(long taskId) {
        new DeleteDataAsyncTask(mTaskDao, taskId).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private static class InsertDataAsyncTask extends AsyncTask<Void, Void, Void> {

        @NonNull
        private final ProjectDao mProjectDao;
        @NonNull
        private final TaskDao mTaskDao;

        private final Task mTask;

        private final Project mProject;

        private final long mLastTaskId;

        private InsertDataAsyncTask(@NonNull ProjectDao projectDao, @NonNull TaskDao taskDao, Task task, Project project, long lastTaskId) {
            mProjectDao = projectDao;
            mTaskDao = taskDao;
            mTask = task;
            mProject = project;
            mLastTaskId = lastTaskId;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (mProject != null) {
                mProjectDao.createProject(mProject);
            }
            if (mTask != null) {
                Task task = new Task(mLastTaskId +1,mTask.getProjectId(), mTask.getName(), mTask.getCreationTimestamp());
                mTaskDao.insertTask(task);
            }

            return null;
        }
    }

    private static class DeleteDataAsyncTask extends AsyncTask<Void, Void, Void> {

        @NonNull
        private final TaskDao mTaskDao;

        private long mTaskId;


        private DeleteDataAsyncTask(@NonNull TaskDao taskDao, long taskId) {
            mTaskDao = taskDao;
            mTaskId = taskId;

        }

        @Override
        protected Void doInBackground(Void... voids) {
            mTaskDao.deleteTask(mTaskId);

            return null;
        }
    }
/*

    private class SortingMethod {
        private int mSortingId;

        private SortingMethod(int sortingId) {
            mSortingId = sortingId;
        }

        private int getSortingId() {
            return mSortingId;
        }

        private void setSortingId(int mSortingId) {
            this.mSortingId = mSortingId;
        }
    }
*/



}
