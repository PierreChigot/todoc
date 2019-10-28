package com.cleanup.todoc.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.cleanup.todoc.model.Project;

import java.util.List;

@Dao
public interface ProjectDao {

    // TODO PIERRE ne pas renvoyer "void" mais "long" pour savoir l'ID de ton project
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void createProject(Project project);

    @Insert
    void insertAll(Project... projects);


    @Query("SELECT * FROM Project")
    LiveData<List<Project>> getProjectsLiveData();

    // TODO PIERRE On aurait peut être préféré voir le term "sync" dans le nom de méthode pour comprendre que c'est une méthode synchrone,
    //  mais il faut la supprimer de toutes façons, on travaille avec les LiveData
    @Query("SELECT * FROM Project")
    List<Project> getProjects();


}
