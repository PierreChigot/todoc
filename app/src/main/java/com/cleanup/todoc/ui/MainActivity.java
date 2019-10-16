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
import com.cleanup.todoc.model.Task;

import java.util.Date;
import java.util.List;


public class MainActivity extends AppCompatActivity implements TasksAdapter.DeleteTaskListener {
    private MainViewModel mViewModel;

    private final Project[] mAllProjects = Project.getAllProjects();

    private final TasksAdapter mAdapter = new TasksAdapter(this);
    @Nullable
    public AlertDialog mDialog = null;
    @Nullable
    private EditText mDialogEditText = null;
    @Nullable
    private Spinner mDialogSpinner = null;
    @NonNull
    private RecyclerView mListTasks;
    @NonNull
    private TextView mLblNoTasks;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mViewModel = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(MainViewModel.class);

        mViewModel.getUiModelsLiveData().observe(this,new Observer<List<TaskUIModel>>() {
            @Override
            public void onChanged(List<TaskUIModel> taskUIModels) {
                mAdapter.submitList(taskUIModels);
            }
        });
        mViewModel.getViewActionMutableLiveData().observe(this, new Observer<ViewAction>() {
            @Override
            public void onChanged(ViewAction viewAction) {
                if (viewAction == ViewAction.NO_TASK){
                    mLblNoTasks.setVisibility(View.VISIBLE);
                    mListTasks.setVisibility(View.GONE);
                }else {
                    mLblNoTasks.setVisibility(View.GONE);
                    mListTasks.setVisibility(View.VISIBLE);
                }
            }
        });

        mListTasks = findViewById(R.id.list_tasks);
        mLblNoTasks = findViewById(R.id.lbl_no_task);

        mListTasks.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mListTasks.setAdapter(mAdapter);

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
            mViewModel.mSortMethod = 1;
            mViewModel.refresh();
        } else if (id == R.id.filter_alphabetical_inverted) {
            mViewModel.mSortMethod = 2;
            mViewModel.refresh();
        } else if (id == R.id.filter_oldest_first) {
            mViewModel.mSortMethod = 3;
            mViewModel.refresh();
        } else if (id == R.id.filter_recent_first) {
            mViewModel.mSortMethod = 4;
            mViewModel.refresh();
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onDeleteTask(long taskId) {
        mViewModel.deleteTask(taskId);

    }
    private void onPositiveButtonClick(DialogInterface dialogInterface) {
        // If mDialog is open
        if (mDialogEditText != null && mDialogSpinner != null) {
            // Get the name of the task
            String taskName = mDialogEditText.getText().toString();

            // Get the selected project to be associated to the task
            Project taskProject = null;
            if (mDialogSpinner.getSelectedItem() instanceof Project) {
                taskProject = (Project) mDialogSpinner.getSelectedItem();
            }
            // If a name has not been set
            if (taskName.trim().isEmpty()) {
                mDialogEditText.setError(getString(R.string.empty_task_name));
            }
            // If both project and name of the task have been set
            else if (taskProject != null) {
                // TODO: Replace this by id of persisted task
                long id = (long) (Math.random() * 50000);
                Task task = new Task(
                        id,
                        taskProject.getId(),
                        taskName,
                        new Date().getTime()
                );
                mViewModel.addTask(task);
                dialogInterface.dismiss();
            }
            // If name has been set, but project has not been set (this should never occur)
            else{
                dialogInterface.dismiss();
            }
        }
        // If mDialog is already closed
        else {
            dialogInterface.dismiss();
        }
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
                        onPositiveButtonClick(mDialog);
                    }
                });
            }
        });

        return mDialog;
    }
        //TODO qui est-ce qui lance ça : activity ou viewmodel?
    private void populateDialogSpinner() {
        final ArrayAdapter<Project> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, mAllProjects);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        if (mDialogSpinner != null) {
            mDialogSpinner.setAdapter(adapter);
        }
    }


}
