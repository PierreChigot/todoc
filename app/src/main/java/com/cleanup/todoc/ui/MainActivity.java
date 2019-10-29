package com.cleanup.todoc.ui;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.cleanup.todoc.R;
import com.cleanup.todoc.model.Project;
import java.util.List;


public class MainActivity extends AppCompatActivity implements TasksAdapter.DeleteTaskListener {

    private MainViewModel mViewModel;


    @Nullable
    private AlertDialog mDialog = null;
    @Nullable
    private EditText mDialogEditText = null;
    @Nullable
    private Spinner mDialogSpinner = null;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final TasksAdapter tasksAdapter = new TasksAdapter(this);
        final RecyclerView listTasks;
        final TextView mLblNoTasks;
        listTasks = findViewById(R.id.list_tasks);
        mLblNoTasks = findViewById(R.id.lbl_no_task);
        listTasks.setLayoutManager(new LinearLayoutManager(this));
        listTasks.setAdapter(tasksAdapter);
        mViewModel = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(MainViewModel.class);

        mViewModel.getUiModelsLiveData().observe(this, new Observer<List<TaskUIModel>>() {
            @Override
            public void onChanged(List<TaskUIModel> taskUIModels) {
                tasksAdapter.submitList(taskUIModels);
            }
        });
        mViewModel.getViewActionLiveData().observe(this, new Observer<ViewAction>() {
            @Override
            public void onChanged(ViewAction viewAction) {
                switch (viewAction) {
                    case ERROR_TASK_NAME:
                        assert mDialogEditText != null;
                        mDialogEditText.setError(getString(R.string.empty_task_name));
                        break;
                    case SHOW_DIALOG:
                        break;
                    case DIALOG_DISMISS:
                        assert mDialog != null;
                        mDialog.dismiss();
                        break;
                }
            }
        });
       mViewModel.getNoTaskLiveData().observe(this, new Observer<Boolean>() {
           @Override
           public void onChanged(Boolean noTask) {
               if (noTask){
                   mLblNoTasks.setVisibility(View.VISIBLE);
                   listTasks.setVisibility(View.GONE);
               }else {
                   mLblNoTasks.setVisibility(View.GONE);
                   listTasks.setVisibility(View.VISIBLE);
               }
           }
       });

        findViewById(R.id.fab_add_task).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddTaskDialog();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.filter_alphabetical) {
            mViewModel.sortingTasks(SortingMethod.ALPHABETICAL);
        } else if (id == R.id.filter_alphabetical_inverted) {
            mViewModel.sortingTasks(SortingMethod.ALPHABETICAL_INVERTED);
        } else if (id == R.id.filter_oldest_first) {
            mViewModel.sortingTasks(SortingMethod.OLD_FIRST);
        } else if (id == R.id.filter_recent_first) {
            mViewModel.sortingTasks(SortingMethod.RECENT_FIRST);
        } else if (id == R.id.filter_project) {
            mViewModel.sortingTasks(SortingMethod.PROJECT);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDeleteTask(long taskId) {
        mViewModel.deleteTask(taskId);
    }

    private void onPositiveButtonClick() {

        assert mDialogEditText != null;
        String taskName = mDialogEditText.getText().toString();
        Project taskProject = null;
        assert mDialogSpinner != null;
        if (mDialogSpinner.getSelectedItem() instanceof Project) {
            taskProject = (Project) mDialogSpinner.getSelectedItem();
        }
        assert taskProject != null;
        mViewModel.addTask(taskProject.getId(), taskName);
    }

    private void showAddTaskDialog() {
        final AlertDialog dialog = getAddTaskDialog();

        dialog.show();

        mDialogEditText = dialog.findViewById(R.id.txt_task_name);
        mDialogSpinner = dialog.findViewById(R.id.project_spinner);

        populateDialogSpinner();
    }


    @NonNull
    private AlertDialog getAddTaskDialog() {
        final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this, R.style.Dialog);

        alertBuilder.setTitle(R.string.add_task);
        alertBuilder.setView(R.layout.dialog_add_task);
        alertBuilder.setPositiveButton(R.string.add, null);
        alertBuilder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                mDialogEditText = null;
                mDialogSpinner = null;
                mDialog = null;
            }
        });

        mDialog = alertBuilder.create();

        // This instead of listener to positive button in order to avoid automatic dismiss
        mDialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialogInterface) {

                Button button = mDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        onPositiveButtonClick();
                    }
                });
            }
        });

        return mDialog;
    }

    private void populateDialogSpinner() {

        final ArrayAdapter<Project> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                //TODO livedata pour projects
                Project.getAllProjects());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        if (mDialogSpinner != null) {
            mDialogSpinner.setAdapter(adapter);
        }
    }


}
