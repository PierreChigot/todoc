package com.cleanup.todocChigot.ui;

import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.cleanup.todocChigot.data.ProjectDao;
import com.cleanup.todocChigot.data.TaskDao;
import com.cleanup.todocChigot.model.Project;
import com.cleanup.todocChigot.model.Task;
import com.cleanup.todocChigot.utils.SingleLiveEvent;
import com.cleanup.todocChigot.utils.TaskComparator;


import java.util.ArrayList;

import java.util.Collections;
import java.util.Date;
import java.util.List;

class MainViewModel extends ViewModel {


    @NonNull
    private final ProjectDao mProjectDao;
    @NonNull
    private final TaskDao mTaskDao;


    private final MediatorLiveData<List<TaskUIModel>> mUiModelsLiveData = new MediatorLiveData<>();
    private final SingleLiveEvent<ViewAction> mSingleLiveEvent = new SingleLiveEvent<>();
    private final MutableLiveData<SortingMethod> mSortingMethodLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mNoTaskLiveData = new MutableLiveData<>();

    private final LiveData<List<Project>> mProjectsLiveData;
    private SortingMethod mSortingMethod;


    MainViewModel(@NonNull ProjectDao projectDao, @NonNull TaskDao taskDao) {
        mProjectDao = projectDao;
        mTaskDao = taskDao;
        wireUpMediator();
        mProjectsLiveData = mProjectDao.getProjectsLiveData();

       /* mProjectsLiveData = Transformations.map(mProjectDao.getProjectsLiveData(), new Function<List<Project>, List<String>>() {
            @Override
            public List<String> apply(List<Project> projects) {
                List<String> projectsName = new ArrayList<>();
                for (Project project : projects) {
                    projectsName.add(project.getName());
                }
                return projectsName;
            }
        });*/
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

    LiveData<List<Project>> getProjectsLiveData() {
        return mProjectsLiveData;
    }


    private void combineProjectsAndTasks(@Nullable List<Project> projects, @Nullable List<Task> tasks, @Nullable SortingMethod sortingMethod) {

        List<TaskUIModel> uiModels = new ArrayList<>();


        if (tasks == null || tasks.isEmpty()|| projects == null || projects.isEmpty()) {
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
                    for (Project project : projects) {
                        if (project.getId() == task.getProjectId()) {
                            uiModels.add(map(project, task));
                        }
                    }
                }
                break;
            case ALPHABETICAL_INVERTED:
                Collections.sort(tasks, new TaskComparator.TaskZAComparator());
                for (Task task : tasks){
                    for (Project project : projects){
                        if (project.getId() == task.getProjectId()) {
                            uiModels.add(map(project, task));
                        }
                    }
                }
                break;
            case OLD_FIRST:
                Collections.sort(tasks, new TaskComparator.TaskOldComparator());
                for (Task task : tasks) {
                    for (Project project : projects) {
                        if (project.getId() == task.getProjectId()) {
                            uiModels.add(map(project, task));
                        }
                    }
                }
                break;
            case RECENT_FIRST:
                Collections.sort(tasks, new TaskComparator.TaskRecentComparator());
                for (Task task : tasks) {
                    for (Project project : projects) {
                        if (project.getId() == task.getProjectId()) {
                            uiModels.add(map(project, task));
                        }
                    }
                }
                break;
            case PROJECT:
                for (Project project : projects) {
                    for (Task task : tasks) {
                        if (project.getId() == task.getProjectId()) {
                            uiModels.add(map(project, task));
                        }
                    }
                }
                break;
        }
        mUiModelsLiveData.setValue(uiModels);
    }


    void sortingTasks(SortingMethod sortingType) {
        mSortingMethod = sortingType;

        mSortingMethodLiveData.setValue(mSortingMethod);

    }

    private TaskUIModel map(Project project, Task task) {

        long id = task.getId();
        String name = task.getName();
        int color = project.getColor();
        mNoTaskLiveData.setValue(false);
        return new TaskUIModel(id, name, color);
    }

    void addTask(long projectId, String taskName) {

        if (taskName == null || taskName.isEmpty()) {
            mSingleLiveEvent.setValue(ViewAction.ERROR_TASK_NAME);
        } else {
            Task task = new Task(projectId, taskName, new Date().getTime());
            new InsertDataAsyncTask(mTaskDao, task, projectId).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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


        private InsertDataAsyncTask(@NonNull TaskDao taskDao,
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

        private final long mTaskId;


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
