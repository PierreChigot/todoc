package com.cleanup.todocChigot.viewModel;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.cleanup.todocChigot.data.AppDatabase;
import com.cleanup.todocChigot.data.ProjectDao;
import com.cleanup.todocChigot.data.TaskDao;

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

    public static ViewModelFactory getInstance(Context context) {
        if (sFactory == null) {
            synchronized (ViewModelFactory.class) {
                if (sFactory == null) {
                    sFactory = new ViewModelFactory(
                            AppDatabase.getInstance(context).projectDao(),
                            AppDatabase.getInstance(context).taskDao()
                    );
                }
            }
        }

        return sFactory;
    }


    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(MainViewModel.class)) {
            return (T) new MainViewModel(projectDao,taskDao);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
