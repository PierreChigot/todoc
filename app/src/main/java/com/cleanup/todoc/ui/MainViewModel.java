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
import java.util.concurrent.ExecutionException;

class MainViewModel extends ViewModel {


    @NonNull
    private ProjectDao mProjectDao;
    @NonNull
    private TaskDao mTaskDao;

    // TODO PIERRE A supprimer
    private long mLastId = -1;


    private final MediatorLiveData<List<TaskUIModel>> mUiModelsLiveData = new MediatorLiveData<>();
    private final SingleLiveEvent<ViewAction> mSingleLiveDataEvent = new SingleLiveEvent<>();
    private final MutableLiveData<List<Project>>mProjectLiveData = new MutableLiveData<>();
    private final MutableLiveData<Integer> mSortingMethodLiveData = new MutableLiveData<>();


    // TODO PIERRE Un enum ici serait très appréciable pour bien voir les différents types de sorting supportés
    private Integer mSortingMethod = -1;


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

    // TODO PIERRE "mutable" dans le nom de la fonction
    LiveData<ViewAction> getViewActionMutableLiveData() {
        return mSingleLiveDataEvent;
    }

    // TODO PIERRE A supprimer
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
           uiModels = new ArrayList<>();
            //mSingleLiveDataEvent.setValue(ViewAction.NO_TASK);

           // TODO PIERRE C'est là qu'on exposerait un boolean "pas de task" (see MainActivity:93)
           mUiModelsLiveData.setValue(uiModels);
            return;
        }
        if (sortingMethod == null) {
            sortingMethod = 3;
        }

        // TODO PIERRE Un switch serait mieux je pense
        //  Avec un petit coup de refactor aussi
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
            Collections.sort(tasks, new Task.TaskRecentComparator());
            for (Task task : tasks) {
                if (task.getProject() != null) {
                    uiModels.add(map(task.getId(), task.getName(), task.getProject().getColor()));
                }
            }
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
        //mSingleLiveDataEvent.setValue(ViewAction.SHOW_TASKS);
        mProjectLiveData.setValue(projects);
    }


    // TODO PIERRE Attention majuscule
    void SortingTasks(int sortingType) {
        mSortingMethod = sortingType;

        mSortingMethodLiveData.setValue(mSortingMethod);

    }

    // TODO PIERRE Il faut que tu passes le project et la task dans cette fonction
    //  et que tu fasses ton mapping dedans en allant piocher dans chacun des objets les informations qu'il te faut
    private TaskUIModel map(long id, String name, int color) {

        return new TaskUIModel(id, name, color);
    }

    void addTask(Task task) {

        new InsertDataAsyncTask(mProjectDao, mTaskDao, task, null, mLastId).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    /*private void addProject(Project project) {

        new InsertDataAsyncTask(mProjectDao, mTaskDao, null, project,mLastId).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }*/
    // TODO PIERRE A supprimer
    List<Project>getProjets() throws ExecutionException, InterruptedException {
        return new GetProjectsDataAsyncTask(mProjectDao).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR).get();

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

        // TODO PIERRE A supprimer
        private final long mLastTaskId;

        // TODO PIERRE Annotations @NonNull
        private InsertDataAsyncTask(@NonNull ProjectDao projectDao, @NonNull TaskDao taskDao, Task task, Project project, long lastTaskId) {
            mProjectDao = projectDao;
            mTaskDao = taskDao;
            mTask = task;
            mProject = project;
            mLastTaskId = lastTaskId;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            // TODO PIERRE Annotations @NonNull
            if (mProject != null) {
                // TODO PIERRE
                //  long idNewProject = mProjectDao.createProject(mProject);
                mProjectDao.createProject(mProject);
            }
            // TODO PIERRE Annotations @NonNull
            if (mTask != null) {
                // TODO PIERRE Non nécessaire
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

    // TODO Classe à supprimer
    private static class GetProjectsDataAsyncTask extends AsyncTask<Void, Void, List<Project>> {

        @NonNull
        private final ProjectDao mProjectDao;

        private GetProjectsDataAsyncTask(@NonNull ProjectDao projectDao) {
           mProjectDao = projectDao;

        }

        @Override
        protected List<Project> doInBackground(Void... voids) {
            return mProjectDao.getProjects();
        }


    }




}
