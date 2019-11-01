package com.cleanup.todoc.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cleanup.todoc.R;


public class TasksAdapter extends ListAdapter<TaskUIModel, TasksAdapter.ViewHolder> {




    @NonNull
    private final DeleteTaskListener mDeleteTaskListener;


    TasksAdapter(@NonNull DeleteTaskListener deleteTaskListener) {
        super(new DiffCallback());
        mDeleteTaskListener = deleteTaskListener;

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_task, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position), mDeleteTaskListener);

    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView mNameTask;
        private final AppCompatImageView mImgProject;
        private final TextView mProjectName;
        private final AppCompatImageView mDeleteButton;


        ViewHolder(@NonNull View itemView) {
            super(itemView);
            mNameTask = itemView.findViewById(R.id.lbl_task_name);
            mImgProject = itemView.findViewById(R.id.img_project);
            mProjectName = itemView.findViewById(R.id.lbl_project_name);
            mDeleteButton = itemView.findViewById(R.id.img_delete);
        }

        void bind(final TaskUIModel model, final DeleteTaskListener listener) {
            mNameTask.setText(model.getName());

            mImgProject.setSupportImageTintList(ColorStateList.valueOf(model.getColorInt()));
            //mProjectName.setText(taskProject.getName());

            //imgProject.setVisibility(View.INVISIBLE);
            //lblProjectName.setText("");


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





