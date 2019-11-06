package com.cleanup.todocChigot.ui;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


class TaskUIModel {
    private final long mId;
    @NonNull
    private final String mName;
    @ColorInt
    private final int mColorInt;


    TaskUIModel(long id, @NonNull String name, @ColorInt int colorInt) {
        this.mId = id;
        this.mName = name;
        this.mColorInt = colorInt;
    }

    public long getId() {
        return mId;
    }

    @NonNull
    public String getName() {
        return mName;
    }

    @ColorInt
    int getColorInt() {
        return mColorInt;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaskUIModel that = (TaskUIModel) o;
        return mId == that.mId &&
                mName.equals(that.mName) && mColorInt == that.mColorInt;

    }
}
