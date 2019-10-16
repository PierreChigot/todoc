package com.cleanup.todoc.ui;

import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.cleanup.todoc.data.ProjectDao;
import com.cleanup.todoc.data.TaskDao;
import com.cleanup.todoc.model.Project;
import com.cleanup.todoc.model.Task;
import com.cleanup.todoc.utils.SingleLiveEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainViewModel extends ViewModel {


    @NonNull
    private ProjectDao mProjectDao;
    @NonNull
    private TaskDao mTaskDao;



    private final MutableLiveData<List<TaskUIModel>> mUiModelsLiveData = new MutableLiveData<>();
    private final SingleLiveEvent<ViewAction> mSingleLiveDataEvent = new SingleLiveEvent<>();


    int mSortMethod = -1;


    public MainViewModel(@NonNull ProjectDao projectDao, @NonNull TaskDao taskDao) {
        mProjectDao = projectDao;
        mTaskDao = taskDao;



    }
    LiveData<List<TaskUIModel>> getUiModelsLiveData() {
        return mUiModelsLiveData;
    }
    LiveData<ViewAction> getViewActionMutableLiveData() {
        return mSingleLiveDataEvent;
    }

    void refresh(){
        //TODO on doit observer ces live data : ???
        final LiveData<List<Project>> projectLiveData = mProjectDao.getProjectsLiveData();
        final LiveData<List<Task>> tasksLiveData = mTaskDao.getTasksLiveData();
        List<TaskUIModel> uiModels = new ArrayList<>();

        //TODO comment savoir si la livedata est empty?
        //TODO repository or not repository ???
        //TODO créer un mock ou remplir la DB si la Table Project est vide ?

        List<Task> updatedTasks ;
        if (tasksLiveData == null){
            mSingleLiveDataEvent.setValue(ViewAction.NO_TASK);
        }else {
            updatedTasks = tasksLiveData.getValue();
            if (mSortMethod == 1){
                Collections.sort(updatedTasks, new Task.TaskAZComparator());
            }else if (mSortMethod == 2){
                Collections.sort(updatedTasks, new Task.TaskZAComparator());
            }else if (mSortMethod == 3){
                Collections.sort(updatedTasks, new Task.TaskOldComparator());
            }else if (mSortMethod == 4){
                Collections.sort(updatedTasks, new Task.TaskRecentComparator());
            }
            for (Task updatedTask : updatedTasks) {
                int colorRes = -1;

                if (updatedTask.getProjectId() == 1L){
                    colorRes = 0xFFEADAD1;
                }else if (updatedTask.getProjectId() == 2L){
                    colorRes = 0xFFB4CDBA;
                }else if (updatedTask.getProjectId() == 3L) {
                    colorRes = 0xFFA3CED2;
                }
                uiModels.add(new TaskUIModel(updatedTask.getId(), updatedTask.getName(), colorRes));
            }
        }
        mUiModelsLiveData.setValue(uiModels);
    }

    void  addTask(Task task){

        new  InsertDataAsyncTask(mProjectDao, mTaskDao, task).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    void deleteTask(long taskId) {
        mTaskDao.deleteTask(taskId);
    }


    private static class InsertDataAsyncTask extends AsyncTask<Void, Void, Void> {

        @NonNull
        private final ProjectDao mProjectDao;
        @NonNull
        private final TaskDao mTaskDao;

        private final Task mTask;

        private InsertDataAsyncTask(@NonNull ProjectDao projectDao, @NonNull TaskDao taskDao, Task task) {
           mProjectDao = projectDao;
           mTaskDao = taskDao;
           mTask = task;

        }


        @Override
        protected Void doInBackground(Void... voids) {

           mTaskDao.insertTask(mTask);

            return null;
        }
    }
}
