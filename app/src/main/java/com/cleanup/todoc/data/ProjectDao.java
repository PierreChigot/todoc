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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long createProject(Project project);

    @Insert
    void insertAll(Project... projects);


    @Query("SELECT * FROM Project")
    LiveData<List<Project>> getProjectsLiveData();
/*

    @Query("SELECT * FROM Project")
    List<Project> getProjects();
*/


}
