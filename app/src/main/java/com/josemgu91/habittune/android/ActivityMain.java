/*
 * Copyright (C) 2018  José Miguel García Urrutia <josemgu91@gmail.com>
 *
 * This file is part of HabitTune.
 *
 * HabitTune is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * HabitTune is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.josemgu91.habittune.android;

import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;

import com.josemgu91.habittune.R;
import com.josemgu91.habittune.android.navigation.FragmentKey;
import com.josemgu91.habittune.android.navigation.FragmentKeyFactory;
import com.josemgu91.habittune.android.navigation.FragmentStateChanger;
import com.josemgu91.habittune.databinding.ActivityMainBinding;
import com.zhuinden.simplestack.BackstackDelegate;
import com.zhuinden.simplestack.History;
import com.zhuinden.simplestack.StateChange;
import com.zhuinden.simplestack.StateChanger;

public class ActivityMain extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, FragmentInteractionListener, StateChanger {
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    private BackstackDelegate backstackDelegate;
    private FragmentStateChanger fragmentStateChanger;

    private NavigationView navigationView;

    private FragmentKeyFactory fragmentKeyFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        fragmentKeyFactory = new FragmentKeyFactory();
        backstackDelegate = new BackstackDelegate(null);
        backstackDelegate.onCreate(savedInstanceState,
                getLastCustomNonConfigurationInstance(),
                History.single(fragmentKeyFactory.createScheduleKey()));
        backstackDelegate.registerForLifecycleCallbacks(this);

        super.onCreate(savedInstanceState);

        final ActivityMainBinding activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        toolbar = activityMainBinding.includedToolbar.toolbar;
        drawerLayout = activityMainBinding.drawerLayout;
        navigationView = activityMainBinding.navigationView;
        setSupportActionBar(toolbar);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this,
                drawerLayout,
                toolbar,
                R.string.menu_navigation_open_drawer,
                R.string.menu_navigation_close_drawer);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        navigationView.setNavigationItemSelectedListener(this);

        fragmentStateChanger = new FragmentStateChanger(getSupportFragmentManager(), R.id.fragmentContainer, new FragmentKeyFactory.FragmentFactory());
        backstackDelegate.setStateChanger(this);
        actionBarDrawerToggle.setToolbarNavigationClickListener(v -> onBackPressed());
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawers();
            return;
        }
        goBack();
    }

    private void goBack() {
        if (!backstackDelegate.onBackPressed()) {
            super.onBackPressed();
        }
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return backstackDelegate.onRetainCustomNonConfigurationInstance();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        actionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigationMenuGoToSchedule:
                backstackDelegate.getBackstack().jumpToRoot();
                break;
            case R.id.navigationMenuGoToRoutines:
                backstackDelegate.getBackstack().setHistory(History.of(fragmentKeyFactory.createScheduleKey(), fragmentKeyFactory.createRoutinesKey()), StateChange.REPLACE);
                break;
            case R.id.navigationMenuGoToActivities:
                backstackDelegate.getBackstack().setHistory(History.of(fragmentKeyFactory.createScheduleKey(), fragmentKeyFactory.createActivitiesKey()), StateChange.REPLACE);
                break;
            case R.id.navigationMenuGoToStatistics:
                backstackDelegate.getBackstack().setHistory(History.of(fragmentKeyFactory.createScheduleKey(), fragmentKeyFactory.createStatisticsKey()), StateChange.REPLACE);
                break;
            case R.id.navigationMenuGoToSettings:
                backstackDelegate.getBackstack().goTo(fragmentKeyFactory.createSettingsKey());
                break;
            case R.id.navigationMenuGoToHelp:
                backstackDelegate.getBackstack().goTo(fragmentKeyFactory.createHelpKey());
                break;
        }
        drawerLayout.closeDrawers();
        return true;
    }

    @Override
    public void handleStateChange(@NonNull StateChange stateChange, @NonNull Callback completionCallback) {
        Log.d("ActivityMain", "State change: " +
                (stateChange.getDirection() == StateChange.REPLACE ? "Replace" :
                        stateChange.getDirection() == StateChange.BACKWARD ? "Backward" :
                                stateChange.getDirection() == StateChange.FORWARD ? "Forward" :
                                        "Unknown")
        );
        if (stateChange.topNewState().equals(stateChange.topPreviousState())) {
            completionCallback.stateChangeComplete();
            return;
        }
        final FragmentKey topStateKey = stateChange.topNewState();
        switch (topStateKey.getFragmentTag()) {
            case FragmentKeyFactory.FRAGMENT_TAG_SCHEDULE:
                navigationView.setCheckedItem(R.id.navigationMenuGoToSchedule);
                break;
            case FragmentKeyFactory.FRAGMENT_TAG_ROUTINES:
                navigationView.setCheckedItem(R.id.navigationMenuGoToRoutines);
                break;
            case FragmentKeyFactory.FRAGMENT_TAG_ACTIVITIES:
                navigationView.setCheckedItem(R.id.navigationMenuGoToActivities);
                break;
            case FragmentKeyFactory.FRAGMENT_TAG_STATISTICS:
                navigationView.setCheckedItem(R.id.navigationMenuGoToStatistics);
                break;
            case FragmentKeyFactory.FRAGMENT_TAG_SETTINGS:
                navigationView.setCheckedItem(R.id.navigationMenuGoToSettings);
                break;
            case FragmentKeyFactory.FRAGMENT_TAG_HELP:
                navigationView.setCheckedItem(R.id.navigationMenuGoToHelp);
                break;
        }
        fragmentStateChanger.handleStateChange(stateChange);
        completionCallback.stateChangeComplete();
    }

    @Override
    public void updateToolbar(String title, int toolbarToggleIcon) {
        toolbar.setTitle(title);
        switch (toolbarToggleIcon) {
            case FragmentInteractionListener.IC_NAVIGATION_CLOSE:
                actionBarDrawerToggle.setDrawerIndicatorEnabled(false);
                actionBarDrawerToggle.setHomeAsUpIndicator(R.drawable.ic_navigation_close_black_24dp);
                break;
            case FragmentInteractionListener.IC_NAVIGATION_HAMBURGUER:
                actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
                break;
            case FragmentInteractionListener.IC_NAVIGATION_UP:
                actionBarDrawerToggle.setDrawerIndicatorEnabled(false);
                actionBarDrawerToggle.setHomeAsUpIndicator(R.drawable.ic_navigation_up_black_24dp);
                break;
            default:
                throw new RuntimeException("Unknown toolbarToggleIcon constant");
        }
    }

    @Override
    public void updateNavigationDrawer(boolean allowOpening) {
        if (allowOpening) {
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        } else {
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }
    }

    @Override
    public void navigateToFragmentNewActivity() {
        backstackDelegate.getBackstack().goTo(fragmentKeyFactory.createNewActivityKey());
    }

    @Override
    public void navigateToFragmentNewRoutine() {
        backstackDelegate.getBackstack().goTo(fragmentKeyFactory.createNewRoutineKey());
    }

    @Override
    public void navigateToFragmentTagEditor() {
        backstackDelegate.getBackstack().goTo(fragmentKeyFactory.createTagEditorKey());
    }

    @Override
    public void navigateToFragmentRoutineEditor() {
        backstackDelegate.getBackstack().goTo(fragmentKeyFactory.createRoutineEditorKey());
    }

    @Override
    public void showToolbarTextInput() {
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(R.layout.toolbar_text_input);
        actionBar.setDisplayShowTitleEnabled(false);
    }

    @Override
    public EditText getToolbarTextInput() {
        final ActionBar actionBar = getSupportActionBar();
        return actionBar.getCustomView().findViewById(R.id.editTextTextInput);
    }

    @Override
    public void hideToolbarTextInput() {
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(false);
        actionBar.setDisplayShowTitleEnabled(true);
    }

    @Override
    public void finishFragment() {
        goBack();
    }
}
