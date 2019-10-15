package com.cleanup.todoc.ui;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.cleanup.todoc.data.AppDatabase;
import com.cleanup.todoc.data.ProjectDao;
import com.cleanup.todoc.data.TaskDao;

public class ViewModelFactory implements ViewModelProvider.Factory {

    private static ViewModelFactory sFactory;

    @NonNull
    private final ProjectDao projectDao;
    @NonNull
    private final TaskDao taskDao;


    private ViewModelFactory(@NonNull ProjectDao projectDao, @NonNull TaskDao taskDao) {
       this.projectDao = projectDao;
       this.taskDao = taskDao;
    }

    public static ViewModelFactory getInstance() {
        if (sFactory == null) {
            synchronized (ViewModelFactory.class) {
                if (sFactory == null) {
                    sFactory = new ViewModelFactory(
                            AppDatabase.getInstance().projectDao(),
                            AppDatabase.getInstance().taskDao()
                    );
                }
            }
        }

        return sFactory;
    }


    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(MainViewModel.class)) {
            return (T) new MainViewModel(projectDao,taskDao);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
