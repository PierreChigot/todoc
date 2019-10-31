package com.cleanup.todoc.ui;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


public class TaskUIModel {
    private final long mId;
    @NonNull
    private final String mName;
    @ColorInt
    private final int mColorRes;


    TaskUIModel(long id, @NonNull String name, @ColorInt int colorRes) {
        this.mId = id;
        this.mName = name;
        this.mColorRes = colorRes;
    }

    public long getId() {
        return mId;
    }

    @NonNull
    public String getName() {
        return mName;
    }

    @ColorInt
    int getColorRes() {
        return mColorRes;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaskUIModel that = (TaskUIModel) o;
        return mId == that.mId &&
                mName.equals(that.mName) && mColorRes == that.mColorRes;

    }
}
