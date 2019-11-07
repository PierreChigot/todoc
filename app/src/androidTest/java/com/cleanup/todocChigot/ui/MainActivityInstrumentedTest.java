package com.cleanup.todocChigot.ui;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;

import androidx.test.espresso.matcher.RootMatchers;
import androidx.test.rule.ActivityTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.view.View;

import android.widget.TextView;

import com.cleanup.todocChigot.R;
import com.cleanup.todocChigot.data.AppDatabase;

import com.cleanup.todocChigot.data.TaskDao;

import org.junit.After;
import org.junit.Before;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;



import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;

import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static com.cleanup.todocChigot.ui.TestUtils.withRecyclerView;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.equalTo;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;


@RunWith(AndroidJUnit4.class)
public class MainActivityInstrumentedTest {

    private AppDatabase mDatabase;

    @Rule
    public final ActivityTestRule<MainActivity> rule = new ActivityTestRule<>(MainActivity.class);


    @Before
    public void createDb() {
        rule.getActivity().deleteDatabase("Database.db");
        Context context = ApplicationProvider.getApplicationContext();
        this.mDatabase = Room.inMemoryDatabaseBuilder(context,
                AppDatabase.class).allowMainThreadQueries().build();
        TaskDao taskDao = mDatabase.taskDao();
        taskDao.deleteAll();
    }

    @After
    public void closeDb() {
        mDatabase.close();
    }

    @Test
    public void addAndRemoveTask() {

        MainActivity activity = rule.getActivity();
        TextView lblNoTask = activity.findViewById(R.id.lbl_no_task);
        RecyclerView listTasks = activity.findViewById(R.id.list_tasks);


        onView(withId(R.id.fab_add_task)).perform(click());
        onView(withId(R.id.txt_task_name)).perform(replaceText("Tâche example"));
        onView(withId(android.R.id.button1)).perform(click());

        // Check that lblTask is not displayed anymore
        assertThat(lblNoTask.getVisibility(), equalTo(View.GONE));
        // Check that recyclerView is displayed
        assertThat(listTasks.getVisibility(), equalTo(View.VISIBLE));
        // Check that it contains one element only
        assertNotNull(listTasks.getAdapter());
        assertThat(listTasks.getAdapter().getItemCount(), equalTo(1));

        onView(withId(R.id.img_delete)).perform(click());

        // Check that lblTask is displayed
        assertThat(lblNoTask.getVisibility(), equalTo(View.VISIBLE));
        // Check that recyclerView is not displayed anymore
        assertThat(listTasks.getVisibility(), equalTo(View.GONE));

    }

    @Test
    public void sortTasks() {

        onView(withId(R.id.fab_add_task)).perform(click());
        onView(withId(R.id.txt_task_name)).perform(replaceText("aaa Tâche example"));
        onView(withId(android.R.id.button1)).perform(click());
        onView(withId(R.id.fab_add_task)).perform(click());
        onView(withId(R.id.txt_task_name)).perform(replaceText("zzz Tâche example"));
        onView(withId(android.R.id.button1)).perform(click());
        onView(withId(R.id.fab_add_task)).perform(click());
        onView(withId(R.id.txt_task_name)).perform(replaceText("hhh Tâche example"));
        onView(withId(android.R.id.button1)).perform(click());


        onView(withRecyclerView(R.id.list_tasks).atPositionOnView(0))
                .check(matches(withText("aaa Tâche example")));
        onView(withRecyclerView(R.id.list_tasks).atPositionOnView(1))
                .check(matches(withText("zzz Tâche example")));
        onView(withRecyclerView(R.id.list_tasks).atPositionOnView(2))
                .check(matches(withText("hhh Tâche example")));


        // Sort alphabetical
        onView(withId(R.id.action_filter)).perform(click());
        onView(withText(R.string.sort_alphabetical)).perform(click());
        onView(withRecyclerView(R.id.list_tasks).atPositionOnView(0))
                .check(matches(withText("aaa Tâche example")));
        onView(withRecyclerView(R.id.list_tasks).atPositionOnView(1))
                .check(matches(withText("hhh Tâche example")));
        onView(withRecyclerView(R.id.list_tasks).atPositionOnView(2))
                .check(matches(withText("zzz Tâche example")));

        // Sort alphabetical inverted
        onView(withId(R.id.action_filter)).perform(click());
        onView(withText(R.string.sort_alphabetical_invert)).perform(click());
        onView(withRecyclerView(R.id.list_tasks).atPositionOnView(0))
                .check(matches(withText("zzz Tâche example")));
        onView(withRecyclerView(R.id.list_tasks).atPositionOnView(1))
                .check(matches(withText("hhh Tâche example")));
        onView(withRecyclerView(R.id.list_tasks).atPositionOnView(2))
                .check(matches(withText("aaa Tâche example")));

        // Sort old first
        onView(withId(R.id.action_filter)).perform(click());
        onView(withText(R.string.sort_oldest_first)).perform(click());
        onView(withRecyclerView(R.id.list_tasks).atPositionOnView(0))
                .check(matches(withText("aaa Tâche example")));
        onView(withRecyclerView(R.id.list_tasks).atPositionOnView(1))
                .check(matches(withText("zzz Tâche example")));
        onView(withRecyclerView(R.id.list_tasks).atPositionOnView(2))
                .check(matches(withText("hhh Tâche example")));

        // Sort recent first
        onView(withId(R.id.action_filter)).perform(click());
        onView(withText(R.string.sort_recent_first)).perform(click());
        onView(withRecyclerView(R.id.list_tasks).atPositionOnView(0))
                .check(matches(withText("hhh Tâche example")));
        onView(withRecyclerView(R.id.list_tasks).atPositionOnView(1))
                .check(matches(withText("zzz Tâche example")));
        onView(withRecyclerView(R.id.list_tasks).atPositionOnView(2))
                .check(matches(withText("aaa Tâche example")));
    }

    @Test
    public void sortTasksByProject() {

        onView(withId(R.id.fab_add_task)).perform(click());
        onView(withId(R.id.txt_task_name)).perform(replaceText("aaa Tâche project 1"));
        onView(withId(R.id.project_spinner)).perform(click());
        onData(anything()).inRoot(RootMatchers.isPlatformPopup()).atPosition(0).perform(click());
        onView(withId(android.R.id.button1)).perform(click());

        onView(withId(R.id.fab_add_task)).perform(click());
        onView(withId(R.id.txt_task_name)).perform(replaceText("aaa Tâche project 3"));
        onView(withId(R.id.project_spinner)).perform(click());
        onData(anything()).inRoot(RootMatchers.isPlatformPopup()).atPosition(2).perform(click());
        onView(withId(android.R.id.button1)).perform(click());

        onView(withId(R.id.fab_add_task)).perform(click());
        onView(withId(R.id.txt_task_name)).perform(replaceText("bbb Tâche project 1"));
        onView(withId(R.id.project_spinner)).perform(click());
        onData(anything()).inRoot(RootMatchers.isPlatformPopup()).atPosition(0).perform(click());
        onView(withId(android.R.id.button1)).perform(click());

        onView(withId(R.id.fab_add_task)).perform(click());
        onView(withId(R.id.txt_task_name)).perform(replaceText("aaa Tâche project 2"));
        onView(withId(R.id.project_spinner)).perform(click());
        onData(anything()).inRoot(RootMatchers.isPlatformPopup()).atPosition(1).perform(click());
        onView(withId(android.R.id.button1)).perform(click());

        onView(withId(R.id.fab_add_task)).perform(click());
        onView(withId(R.id.txt_task_name)).perform(replaceText("bbb Tâche project 3"));
        onView(withId(R.id.project_spinner)).perform(click());
        onData(anything()).inRoot(RootMatchers.isPlatformPopup()).atPosition(2).perform(click());
        onView(withId(android.R.id.button1)).perform(click());

        onView(withId(R.id.fab_add_task)).perform(click());
        onView(withId(R.id.txt_task_name)).perform(replaceText("bbb Tâche project 2"));
        onView(withId(R.id.project_spinner)).perform(click());
        onData(anything()).inRoot(RootMatchers.isPlatformPopup()).atPosition(1).perform(click());
        onView(withId(android.R.id.button1)).perform(click());


        onView(withId(R.id.action_filter)).perform(click());
        onView(withText(R.string.sort_project)).perform(click());
        onView(withRecyclerView(R.id.list_tasks).atPositionOnView(0))
                .check(matches(withText("aaa Tâche project 1")));
        onView(withRecyclerView(R.id.list_tasks).atPositionOnView(1))
                .check(matches(withText("bbb Tâche project 1")));
        onView(withRecyclerView(R.id.list_tasks).atPositionOnView(2))
                .check(matches(withText("aaa Tâche project 2")));
        onView(withRecyclerView(R.id.list_tasks).atPositionOnView(3))
                .check(matches(withText("bbb Tâche project 2")));
        onView(withRecyclerView(R.id.list_tasks).atPositionOnView(4))
                .check(matches(withText("aaa Tâche project 3")));
        onView(withRecyclerView(R.id.list_tasks).atPositionOnView(5))
                .check(matches(withText("bbb Tâche project 3")));


    }

}
