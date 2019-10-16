package com.cleanup.todoc.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.cleanup.todoc.model.Task;

import java.util.List;
@Dao
public interface TaskDao {

    @Query("SELECT * FROM Task")
    LiveData<List<Task>> getTasksLiveData();

    @Insert
    long insertTask(Task task);

    @Update
    int updateTask(Task item);

    @Query("DELETE FROM Task WHERE id = :taskId")
    int deleteTask(long taskId);
}
