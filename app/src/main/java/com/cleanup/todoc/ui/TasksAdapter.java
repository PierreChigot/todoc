package com.cleanup.todoc.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cleanup.todoc.R;
import com.cleanup.todoc.model.Task;

import java.util.List;

public class TasksAdapter extends ListAdapter<TaskUIModel, TasksAdapter.ViewHolder> {

    @NonNull
    private List<Task> tasks;


    @NonNull
    private final DeleteTaskListener deleteTaskListener;


    TasksAdapter(DeleteTaskListener deleteTaskListener) {
        super(new DiffCallback());
        this.deleteTaskListener = deleteTaskListener;

    }

    void updateTasks(@NonNull final List<Task> tasks) {
        this.tasks = tasks;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_task, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView mNameTask;
        private final AppCompatImageView mImgProject;
        private final TextView mProjectName;
        private final AppCompatImageView mDeleteButton;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mNameTask = itemView.findViewById(R.id.lbl_task_name);
            mImgProject = itemView.findViewById(R.id.img_project);
            mProjectName = itemView.findViewById(R.id.lbl_project_name);
            mDeleteButton = itemView.findViewById(R.id.img_delete);
        }

        void bind(final TaskUIModel model, final DeleteTaskListener listener) {
            mNameTask.setText(model.getName());

            mDeleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onDeleteTask(model.getId());

                }
            });

        }
    }
    private static class DiffCallback extends DiffUtil.ItemCallback<TaskUIModel> {


        @Override
        public boolean areItemsTheSame(@NonNull TaskUIModel oldItem, @NonNull TaskUIModel newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull TaskUIModel oldItem, @NonNull TaskUIModel newItem) {
            return oldItem.equals(newItem);
        }
    }
    public interface DeleteTaskListener {

        void onDeleteTask(long taskId);
    }
}





