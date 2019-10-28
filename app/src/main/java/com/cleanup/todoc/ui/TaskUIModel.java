package com.cleanup.todoc.ui;

import androidx.annotation.ColorRes;
import androidx.annotation.Nullable;


public class TaskUIModel {
    private final long id;

    // TODO PIERRE Annotations @NonNull
    private final String name;
    @ColorRes
    private final int colorRes;

    // TODO PIERRE Annotations @NonNull
    public TaskUIModel(long id, String name, @ColorRes int colorRes) {
        this.id = id;
        this.name = name;
        this.colorRes = colorRes;
    }

    public long getId() {
        return id;
    }

    // TODO PIERRE Annotations @NonNull
    public String getName() {
        return name;
    }

    // TODO PIERRE Annotations @ColorRes
    public int getColorRes() {
        return colorRes;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaskUIModel that = (TaskUIModel) o;
        return id == that.id &&
                name.equals(that.name) && colorRes == that.colorRes;

    }
}
