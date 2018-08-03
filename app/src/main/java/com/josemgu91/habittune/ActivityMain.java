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

package com.josemgu91.habittune;

import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.josemgu91.habittune.databinding.ActivityMainBinding;
import com.zhuinden.simplestack.BackstackDelegate;
import com.zhuinden.simplestack.History;
import com.zhuinden.simplestack.StateChange;
import com.zhuinden.simplestack.StateChanger;

public class ActivityMain extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, FragmentInteractionListener, StateChanger {

    @IdRes
    private final static int DEFAULT_MENU_SELECTION = R.id.navigationMenuGoToSchedule;

    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    //private ActivityMainController activityMainController;

    private BackstackDelegate backstackDelegate;
    private FragmentStateChanger fragmentStateChanger;

    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        backstackDelegate = new BackstackDelegate(null);
        backstackDelegate.onCreate(savedInstanceState,
                getLastCustomNonConfigurationInstance(),
                History.single(new ScheduleKey()));
        backstackDelegate.registerForLifecycleCallbacks(this);
        super.onCreate(savedInstanceState);
        final ActivityMainBinding activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        toolbar = activityMainBinding.includedToolbar.toolbar;
        drawerLayout = activityMainBinding.drawerLayout;
        navigationView = activityMainBinding.navigationView;

        setSupportActionBar(toolbar);
        actionBarDrawerToggle = setupActionBarDrawerToggle();
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(DEFAULT_MENU_SELECTION);

        //activityMainController = new ActivityMainController(getSupportFragmentManager(), R.id.fragmentContainer);

        /*if (savedInstanceState == null) {
            activityMainController.goToSchedule();
        }*/
        fragmentStateChanger = new FragmentStateChanger(getSupportFragmentManager(), R.id.fragmentContainer);
        backstackDelegate.setStateChanger(this);
    }

    private ActionBarDrawerToggle setupActionBarDrawerToggle() {
        return new ActionBarDrawerToggle(this,
                drawerLayout,
                toolbar,
                R.string.menu_navigation_open_drawer,
                R.string.menu_navigation_close_drawer);
    }

    @Override
    public void onBackPressed() {
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
                //activityMainController.goToSchedule();
                backstackDelegate.getBackstack().jumpToRoot();
                break;
            case R.id.navigationMenuGoToRoutines:
                //activityMainController.goToRoutines();
                backstackDelegate.getBackstack().setHistory(History.of(new ScheduleKey(), new RoutinesKey()), StateChange.REPLACE);
                break;
            case R.id.navigationMenuGoToActivities:
                //activityMainController.goToActivities();
                backstackDelegate.getBackstack().setHistory(History.of(new ScheduleKey(), new ActivitiesKey()), StateChange.REPLACE);
                break;
            case R.id.navigationMenuGoToStatistics:
                //activityMainController.goToStatistics();
                break;
            case R.id.navigationMenuGoToSettings:
                //activityMainController.goToSettings();
                break;
            case R.id.navigationMenuGoToHelp:
                //activityMainController.goToHelp();
                break;
        }
        drawerLayout.closeDrawers();
        return true;
    }

    @Override
    public void updateTitle(String title) {
        toolbar.setTitle(title);
    }

    @Override
    public void handleStateChange(@NonNull StateChange stateChange, @NonNull Callback completionCallback) {
        Log.d("ActivityMain", "handleStateChange");
        if (stateChange.topNewState().equals(stateChange.topPreviousState())) {
            completionCallback.stateChangeComplete();
            return;
        }
        final Object topState = stateChange.topNewState();
        if (topState instanceof ScheduleKey) {
            navigationView.setCheckedItem(R.id.navigationMenuGoToSchedule);
        } else if (topState instanceof RoutinesKey) {
            navigationView.setCheckedItem(R.id.navigationMenuGoToRoutines);
        } else if (topState instanceof ActivitiesKey) {
            navigationView.setCheckedItem(R.id.navigationMenuGoToActivities);
        }
        fragmentStateChanger.handleStateChange(stateChange); // handle fragment nav.
        completionCallback.stateChangeComplete();
    }
}
