package com.cleanup.todoc.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import java.util.Comparator;

@Entity(foreignKeys = @ForeignKey(entity = Project.class,parentColumns = "id",childColumns = "projectId"))
public class Task {

    @PrimaryKey(autoGenerate = true)
    private long id = 0;

    private long projectId;

    @NonNull
    private String name;


    private long creationTimestamp;

    /**
     * Instantiates a new Task.
     *  @param projectId         the unique identifier of the project associated to the task to set
     * @param name              the name of the task to set
     * @param creationTimestamp the timestamp when the task has been created to set
     */
    public Task(long projectId, @NonNull String name, long creationTimestamp) {
       this.projectId = projectId;
       this.name = name;
       this.creationTimestamp = creationTimestamp;
    }
    //TODO ne compile pas s'il n'a pas les setters..
    public void setId(long id) {
        this.id = id;
    }

    public void setProjectId(long projectId) {
        this.projectId = projectId;
    }

    public void setCreationTimestamp(long creationTimestamp) {
        this.creationTimestamp = creationTimestamp;
    }

    public long getId() {
        return id;
    }


    @Nullable
    public Project getProject() {
        return Project.getProjectById(projectId);
    }


    @NonNull
    public String getName() {
        return name;
    }


    public void setName(@NonNull String name) {
        this.name = name;
    }



    public long getProjectId() {
        return projectId;
    }

    public long getCreationTimestamp() {
        return creationTimestamp;
    }


}
