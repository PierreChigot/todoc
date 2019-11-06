package com.cleanup.todocChigot.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;


@Entity(foreignKeys = @ForeignKey(entity = Project.class,parentColumns = "id",childColumns = "projectId"))
public class Task {

    @PrimaryKey(autoGenerate = true)
    private long id = 0;
    @ColumnInfo(name = "projectId", index = true)
    private final long projectId;

    @NonNull
    private String name;

    private final long creationTimestamp;

    public Task(long projectId, @NonNull String name, long creationTimestamp) {
       this.projectId = projectId;
       this.name = name;
       this.creationTimestamp = creationTimestamp;
    }
    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
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
