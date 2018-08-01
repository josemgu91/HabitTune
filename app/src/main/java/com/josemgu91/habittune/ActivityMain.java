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
import android.view.MenuItem;

import com.josemgu91.habittune.databinding.ActivityMainBinding;

public class ActivityMain extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, ActivityMainController.MainMenuPresenter {

    @IdRes
    private final static int DEFAULT_MENU_SELECTION = R.id.navigationMenuGoToSchedule;

    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    private ActivityMainController activityMainController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ActivityMainBinding activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        toolbar = activityMainBinding.includedToolbar.toolbar;
        drawerLayout = activityMainBinding.drawerLayout;
        final NavigationView navigationView = activityMainBinding.navigationView;

        setSupportActionBar(toolbar);
        actionBarDrawerToggle = setupActionBarDrawerToggle();
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(DEFAULT_MENU_SELECTION);

        activityMainController = new ActivityMainController(this, getSupportFragmentManager(), R.id.fragmentContainer);
        activityMainController.goToSchedule();
    }

    private ActionBarDrawerToggle setupActionBarDrawerToggle() {
        return new ActionBarDrawerToggle(this,
                drawerLayout,
                toolbar,
                R.string.menu_navigation_open_drawer,
                R.string.menu_navigation_close_drawer);
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
                activityMainController.goToSchedule();
                break;
            case R.id.navigationMenuGoToRoutines:
                activityMainController.goToRoutines();
                break;
            case R.id.navigationMenuGoToActivities:
                activityMainController.goToActivities();
                break;
            case R.id.navigationMenuGoToStatistics:
                activityMainController.goToStatistics();
                break;
            case R.id.navigationMenuGoToSettings:
                activityMainController.goToSettings();
                break;
            case R.id.navigationMenuGoToHelp:
                activityMainController.goToHelp();
                break;
        }
        drawerLayout.closeDrawers();
        return true;
    }

    @Override
    public void onScreenChanged(int toolbarTitle, boolean canOpenDrawer) {
        toolbar.setTitle(toolbarTitle);
    }
}
