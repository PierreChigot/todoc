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
import com.cleanup.todoc.utils.TaskComparator;


import java.util.ArrayList;

import java.util.Collections;
import java.util.Date;
import java.util.List;

class MainViewModel extends ViewModel {


    @NonNull
    private ProjectDao mProjectDao;
    @NonNull
    private TaskDao mTaskDao;


    private final MediatorLiveData<List<TaskUIModel>> mUiModelsLiveData = new MediatorLiveData<>();
    private final SingleLiveEvent<ViewAction> mSingleLiveEvent = new SingleLiveEvent<>();
    private final MutableLiveData<List<Project>> mProjectLiveData = new MutableLiveData<>();
    private final MutableLiveData<SortingMethod> mSortingMethodLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mNoTaskLiveData = new MutableLiveData<>();


    private SortingMethod mSortingMethod;


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
        mUiModelsLiveData.addSource(mSortingMethodLiveData, new Observer<SortingMethod>() {
            @Override
            public void onChanged(SortingMethod sortingMethod) {
                mSortingMethod = sortingMethod;
                combineProjectsAndTasks(liveDataProject.getValue(), liveDataTasks.getValue(), mSortingMethod);
            }
        });
    }


    LiveData<List<TaskUIModel>> getUiModelsLiveData() {
        return mUiModelsLiveData;
    }

    LiveData<ViewAction> getViewActionLiveData() {
        return mSingleLiveEvent;
    }

    LiveData<Boolean> getNoTaskLiveData() {
        return mNoTaskLiveData;
    }


    private void combineProjectsAndTasks(List<Project> projects, List<Task> tasks, SortingMethod sortingMethod) {

        List<TaskUIModel> uiModels = new ArrayList<>();


        if (tasks == null || tasks.isEmpty()) {
            uiModels = new ArrayList<>();
            mUiModelsLiveData.setValue(uiModels);
            mNoTaskLiveData.setValue(true);
            return;
        }
        if (sortingMethod == null) {
            sortingMethod = SortingMethod.OLD_FIRST;
        }
        switch (sortingMethod) {
            case ALPHABETICAL:
                Collections.sort(tasks, new TaskComparator.TaskAZComparator());
                for (Task task : tasks) {
                    if (task.getProject() != null) {
                        uiModels.add(map(task.getId(), task.getName(), task.getProject().getColor()));
                    }
                }
                break;
            case ALPHABETICAL_INVERTED:
                Collections.sort(tasks, new TaskComparator.TaskZAComparator());
                for (Task task : tasks) {
                    if (task.getProject() != null) {
                        uiModels.add(map(task.getId(), task.getName(), task.getProject().getColor()));
                    }
                }
                break;
            case OLD_FIRST:
                Collections.sort(tasks, new TaskComparator.TaskOldComparator());
                for (Task task : tasks) {
                    if (task.getProject() != null) {
                        uiModels.add(map(task.getId(), task.getName(), task.getProject().getColor()));
                    }
                }
                break;
            case RECENT_FIRST:
                Collections.sort(tasks, new TaskComparator.TaskRecentComparator());
                for (Task task : tasks) {
                    if (task.getProject() != null) {
                        uiModels.add(map(task.getId(), task.getName(), task.getProject().getColor()));
                    }
                }
                break;
            case PROJECT:
                for (Project project : projects) {
                    for (Task task : tasks) {
                        if (project.getId() == task.getProjectId()) {
                            uiModels.add(map(project.getId(), task.getName(), project.getColor()));
                        }
                    }
                }
                break;
        }

        mUiModelsLiveData.setValue(uiModels);
        mProjectLiveData.setValue(projects);
    }


    void sortingTasks(SortingMethod sortingType) {
        mSortingMethod = sortingType;

        mSortingMethodLiveData.setValue(mSortingMethod);

    }
    private TaskUIModel map2 (Project project, Task task){
        long id = 0;
        String name = "";
        int color = 0;


        return new TaskUIModel(id, name, color);
    }
    private TaskUIModel map(long id, String name, int color) {
        //TODO Il faut que tu passes le project et la task dans cette fonction
        //  et que tu fasses ton mapping dedans en allant piocher dans chacun des objets les informations qu'il te faut
        mNoTaskLiveData.setValue(false);
        return new TaskUIModel(id, name, color);
    }

    void addTask(long projectId, String taskName) {

        if (taskName == null || taskName.isEmpty()) {
            mSingleLiveEvent.setValue(ViewAction.ERROR_TASK_NAME);
        } else {
            Task task = new Task(projectId, taskName, new Date().getTime());
            new InsertDataAsyncTask(mProjectDao, mTaskDao, task, projectId).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            mSingleLiveEvent.setValue(ViewAction.DIALOG_DISMISS);
        }
    }

    void deleteTask(long taskId) {
        new DeleteDataAsyncTask(mTaskDao, taskId).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private static class InsertDataAsyncTask extends AsyncTask<Void, Void, Void> {

        @NonNull
        private final TaskDao mTaskDao;

        private final Task mTask;

        private final long mProjectId;


        private InsertDataAsyncTask(@NonNull ProjectDao projectDao, @NonNull TaskDao taskDao,
                                    @NonNull Task task, @NonNull Long projectId) {
            mTaskDao = taskDao;
            mTask = task;
            mProjectId = projectId;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Task task = new Task(mProjectId, mTask.getName(), mTask.getCreationTimestamp());
            mTaskDao.insertTask(task);
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

}
